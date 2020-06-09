package com.ser.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.ser.model.entity.OauthAccount;
import com.ser.model.entity.OauthApiProvider;
import com.ser.model.repository.OauthAccountRepository;
import com.ser.model.repository.OauthApiProviderRepository;
import com.ser.rest.request.NewAccountRequest;
import com.ser.rest.response.Responsable;
import com.ser.rest.response.ResponseError;
import com.ser.service.property.PropertiesService;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping(value = "/new")
public class NewAccountController {

    private OauthApiProviderRepository oauthApiProviderRepository;
    private OauthAccountRepository oauthAccountRepository;

    @RequestMapping(value = "/account", method = RequestMethod.POST)
    public ResponseEntity<Responsable> createNewAccount(@RequestBody @Valid NewAccountRequest newAccountRequest) {

        log.info("Received request: " + newAccountRequest);

        if (newAccountRequest.getAccessCode() == null || !newAccountRequest.getAccessCode()
                .equals(PropertiesService.CommonProperties.getAccessCode())) {

            log.error("Response sent: " + ResponseError.ACCESS_DENIED);
            return ResponseEntity.status(ResponseError.ACCESS_DENIED.getCode()).body(ResponseError.ACCESS_DENIED);
        }

        Optional<OauthApiProvider> optionalApiProvider = oauthApiProviderRepository.findByProviderNameIgnoreCase(newAccountRequest.getApiProviderName());

        if (!optionalApiProvider.isPresent()) {

            log.error("Response sent: " + ResponseError.API_PROVIDER_NOT_FOUND);
            return ResponseEntity.status(ResponseError.API_PROVIDER_NOT_FOUND.getCode()).body(ResponseError.API_PROVIDER_NOT_FOUND);
        }

        Optional<OauthAccount> optionalOauthAccount = oauthAccountRepository.findByAccountNameIgnoreCaseAndOauthApiProviderId(

                newAccountRequest.getAccountName(),
                optionalApiProvider.get().getId()
        );

        if (optionalOauthAccount.isPresent()) {

            OauthAccount oauthAccount = optionalOauthAccount.get();

            if (oauthAccount.getIsArchive()) {

                log.info("Account exists and archived " + oauthAccount);

                log.info("Response sent: " + ResponseError.ACCOUNT_EXISTS_BUT_ARCHIVED);
                return ResponseEntity.status(ResponseError.ACCOUNT_EXISTS_BUT_ARCHIVED.getCode()).body(ResponseError.ACCOUNT_EXISTS_BUT_ARCHIVED);
            }

            log.error("Response sent: " + ResponseError.ACCOUNT_EXISTS);
            return ResponseEntity.status(ResponseError.ACCOUNT_EXISTS.getCode()).body(ResponseError.ACCOUNT_EXISTS);
        }


        OauthAccount oauthAccount = new OauthAccount();
        oauthAccount.setOauthApiProvider(optionalApiProvider.get());
        oauthAccount.setAccountName(newAccountRequest.getAccountName());
        oauthAccount.setClientId(newAccountRequest.getClientId());
        oauthAccount.setClientSecret(newAccountRequest.getClientSecret());
        oauthAccount.setResponseType(newAccountRequest.getResponseType());
        oauthAccount.setScope(newAccountRequest.getScope());

        oauthAccountRepository.save(oauthAccount);

        log.info("Response sent: " + ResponseError.ACCOUNT_CREATED);
        return ResponseEntity.status(ResponseError.ACCOUNT_CREATED.getCode()).body(ResponseError.ACCOUNT_CREATED);
    }

    @Autowired
    public NewAccountController(
            OauthApiProviderRepository oauthApiProviderRepository,
            OauthAccountRepository oauthAccountRepository
    ) {
        this.oauthApiProviderRepository = oauthApiProviderRepository;
        this.oauthAccountRepository = oauthAccountRepository;

    }
}
