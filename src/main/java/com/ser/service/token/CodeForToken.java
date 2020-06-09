package com.ser.service.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.ser.model.entity.OauthAccount;
import com.ser.model.repository.OauthAccountRepository;
import com.ser.service.links.LinkSender;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class CodeForToken {

    private OauthAccountRepository oauthAccountRepository;

    @Async
    public void getCode(OauthAccount oauthAccount) {
        registerLink(oauthAccount);
        new LinkSender().sendCodeRequestForAccount(oauthAccount);
    }

    public String registerAndGetTokenLink(OauthAccount oauthAccount) {
        registerLink(oauthAccount);
        new LinkSender().sendCodeRequestForAccount(oauthAccount);

        log.info("Token link has been generated for account: {}", oauthAccount);
        return new LinkSender().buildTokenLink(oauthAccount);
    }

    private synchronized void registerLink(OauthAccount oauthAccount) {
        String state = UUID.randomUUID().toString();
        Timestamp stateExpireDate = Timestamp.valueOf(LocalDateTime.now().plusDays(2L));

        log.info("State for the accountName: " + oauthAccount + " was generated: " + state + " state expire date set to: " + stateExpireDate);
        oauthAccount.setState(state);
        oauthAccount.setStatus("in use");
        oauthAccount.setStateExpireDate(stateExpireDate);
        oauthAccountRepository.save(oauthAccount);
    }

    @Autowired
    public CodeForToken(OauthAccountRepository oauthAccountRepository) {
        this.oauthAccountRepository = oauthAccountRepository;
    }
}
