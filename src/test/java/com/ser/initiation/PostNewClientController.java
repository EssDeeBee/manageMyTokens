package com.ser.initiation;

import com.ser.initiation.request.PostNewClientRequest;
import com.ser.model.entity.OauthAccount;
import com.ser.model.entity.OauthApiProvider;
import com.ser.model.entity.OauthClient;
import com.ser.model.entity.OauthClientToken;
import com.ser.model.repository.OauthAccountRepository;
import com.ser.model.repository.OauthApiProviderRepository;
import com.ser.model.repository.OauthClientRepository;
import com.ser.model.repository.OauthClientTokenRepository;
import com.ser.rest.response.ResponseError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.util.Optional;


@Slf4j
@RestController
@RequestMapping(value = "/new_oauth")
public class PostNewClientController {
    private OauthApiProviderRepository oauthApiProviderRepository;
    private OauthAccountRepository oauthAccountRepository;
    private OauthClientRepository oauthClientRepository;
    private OauthClientTokenRepository oauthClientTokenRepository;

    @RequestMapping(value = "create_client", method = RequestMethod.POST)
    public ResponseEntity createNewOauthClient(@RequestBody PostNewClientRequest postNewClientRequest) {

        log.info("Request received: " + postNewClientRequest);

        if (postNewClientRequest.getApiProvider() == null
                || postNewClientRequest.getAccountName() == null
                || postNewClientRequest.getClientMailId() == null
                || postNewClientRequest.getRefreshToken() == null) {


            log.error("Response sent: " + ResponseError.REQUIRED_PARAM_MISSING.toString());
            return ResponseEntity.status(ResponseError.REQUIRED_PARAM_MISSING.getCode()).body(ResponseError.REQUIRED_PARAM_MISSING);
        }

        Optional<OauthApiProvider> optionalOauthApiProvider = oauthApiProviderRepository.findByProviderNameIgnoreCase(postNewClientRequest.getApiProvider());
        if (!optionalOauthApiProvider.isPresent()) {
            return ResponseEntity.status(ResponseError.API_PROVIDER_NOT_FOUND.getCode()).body(ResponseError.API_PROVIDER_NOT_FOUND);
        }


        Optional<OauthAccount> optionalOauthAccount = oauthAccountRepository
                .findByAccountNameIgnoreCaseAndOauthApiProviderId(
                        postNewClientRequest.getAccountName(),
                        optionalOauthApiProvider.get().getId()
                );

        if (!optionalOauthAccount.isPresent()) {

            log.error(ResponseError.ACCOUNT_NOT_FOUND.toString());
            return ResponseEntity.status(ResponseError.ACCOUNT_NOT_FOUND.getCode()).body(ResponseError.ACCOUNT_NOT_FOUND);
        }

        Optional<OauthClient> existingOauthClient = oauthClientRepository.findByClientMailIdIgnoreCaseAndOauthAccountId(
                postNewClientRequest.getClientMailId(),
                optionalOauthAccount.get().getId()
        );

        if (existingOauthClient.isPresent()) {
            log.error(ResponseError.REQUESTED_ACCOUNT_EXISTS.toString());
            return ResponseEntity.status(ResponseError.REQUESTED_CLIENT_EXISTS.getCode()).body(ResponseError.REQUESTED_CLIENT_EXISTS);

        }

        OauthClient oauthClient = new OauthClient();
        oauthClient.setOauthAccount(optionalOauthAccount.get());
        oauthClient.setClientName(postNewClientRequest.getClientName() == null ? "" : postNewClientRequest.getAccountName());
        oauthClient.setClientMailId(postNewClientRequest.getClientMailId());


        oauthClientRepository.save(oauthClient);

        OauthClientToken oauthClientToken = new OauthClientToken();
        oauthClientToken.setOauthClient(oauthClient);
        oauthClientToken.setAccessToken(postNewClientRequest.getAccessToken() == null ? "" : postNewClientRequest.getAccessToken());
        oauthClientToken.setRefreshToken(postNewClientRequest.getRefreshToken());
        oauthClientToken.setTokenExpiresIn(1L);
        oauthClientToken.setTokenType("Bearer");
        oauthClientTokenRepository.save(oauthClientToken);


        return ResponseEntity.status(HttpStatus.CREATED).body(oauthClientToken);
    }

    @Autowired
    public PostNewClientController(
            OauthApiProviderRepository oauthApiProviderRepository,
            OauthAccountRepository oauthAccountRepository,
            OauthClientRepository oauthClientRepository,
            OauthClientTokenRepository oauthClientTokenRepository
    ) {
        this.oauthApiProviderRepository = oauthApiProviderRepository;
        this.oauthAccountRepository = oauthAccountRepository;
        this.oauthClientRepository = oauthClientRepository;
        this.oauthClientTokenRepository = oauthClientTokenRepository;
    }

}
