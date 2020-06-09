package com.ser.initiation;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.ser.initiation.request.PostNewAccountRequest;
import com.ser.model.entity.OauthAccount;
import com.ser.model.entity.OauthAccountToken;
import com.ser.model.entity.OauthApiProvider;
import com.ser.model.repository.OauthAccountRepository;
import com.ser.model.repository.OauthAccountTokenRepository;
import com.ser.model.repository.OauthApiProviderRepository;
import com.ser.rest.response.ResponseError;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping(value = "/new_oauth")
public class PostNewAccountController {

    private OauthApiProviderRepository ouathApiProvderRepository;
    private OauthAccountRepository oauthAccountRepository;
    private OauthAccountTokenRepository oauthAccountTokenRepository;

    @RequestMapping(value = "create_account", method = RequestMethod.POST)
    public ResponseEntity createNewOauthAccount(@RequestBody PostNewAccountRequest postNewAccountRequest) {

        log.info("Request received: " + postNewAccountRequest);

        if (postNewAccountRequest.getAccountName() == null
                || postNewAccountRequest.getApiProvider() == null
                || postNewAccountRequest.getClientId() == null
                || postNewAccountRequest.getClientSecret() == null
                || postNewAccountRequest.getRefreshToken() == null) {


            log.error("Response sent: " + ResponseError.REQUIRED_PARAM_MISSING.toString());
            return ResponseEntity.status(ResponseError.REQUIRED_PARAM_MISSING.getCode()).body(ResponseError.REQUIRED_PARAM_MISSING);
        }

        Optional<OauthApiProvider> optionalOauthApiProvider = ouathApiProvderRepository.findByProviderNameIgnoreCase(postNewAccountRequest.getApiProvider());
        if (!optionalOauthApiProvider.isPresent()) {
            return ResponseEntity.status(ResponseError.API_PROVIDER_NOT_FOUND.getCode()).body(ResponseError.API_PROVIDER_NOT_FOUND);
        }


        Optional<OauthAccount> existingOauthAccount = oauthAccountRepository
                .findByAccountNameIgnoreCaseAndOauthApiProviderId(
                        postNewAccountRequest.getAccountName(),
                        optionalOauthApiProvider.get().getId()
                );

        if (existingOauthAccount.isPresent()) {

            log.error(ResponseError.REQUESTED_ACCOUNT_EXISTS.toString());
            return ResponseEntity.status(ResponseError.REQUESTED_ACCOUNT_EXISTS.getCode())
                    .body(ResponseError.REQUESTED_ACCOUNT_EXISTS);
        }

        OauthAccount oauthAccount = new OauthAccount();
        oauthAccount.setOauthApiProvider(optionalOauthApiProvider.get());
        oauthAccount.setAccountName(postNewAccountRequest.getAccountName());
        oauthAccount.setClientId(postNewAccountRequest.getClientId());
        oauthAccount.setClientSecret(postNewAccountRequest.getClientSecret());
        oauthAccount.setResponseType(postNewAccountRequest.getResponseType() == null ? "code" : postNewAccountRequest.getResponseType());
        oauthAccount.setScope(postNewAccountRequest.getScope() == null ? "" : postNewAccountRequest.getScope());
        oauthAccount.setState(postNewAccountRequest.getState() == null ? "" : postNewAccountRequest.getState());
        oauthAccount.setStatus(postNewAccountRequest.getStatus() == null ? "" : postNewAccountRequest.getStatus());

        oauthAccountRepository.save(oauthAccount);

        OauthAccountToken oauthAccountToken = new OauthAccountToken();
        oauthAccountToken.setOauthAccount(oauthAccount);
        oauthAccountToken.setAccessToken(postNewAccountRequest.getAccessToken() == null ? "" : postNewAccountRequest.getAccessToken());
        oauthAccountToken.setRefreshToken(postNewAccountRequest.getRefreshToken());
        oauthAccountToken.setTokenExpiresIn(1L);
        oauthAccountToken.setTokenType("Bearer");
        oauthAccountTokenRepository.save(oauthAccountToken);


        return ResponseEntity.status(HttpStatus.CREATED).body(oauthAccountToken);
    }

    @Autowired
    public PostNewAccountController(
            OauthApiProviderRepository ouathApiProvderRepository,
            OauthAccountRepository oauthAccountRepository,
            OauthAccountTokenRepository oauthAccountTokenRepository
    ) {
        this.ouathApiProvderRepository = ouathApiProvderRepository;
        this.oauthAccountRepository = oauthAccountRepository;
        this.oauthAccountTokenRepository = oauthAccountTokenRepository;
    }

}
