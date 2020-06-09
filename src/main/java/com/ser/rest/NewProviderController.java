package com.ser.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.ser.model.entity.OauthApiProvider;
import com.ser.model.repository.OauthApiProviderRepository;
import com.ser.rest.request.NewApiProviderRequest;
import com.ser.rest.response.Responsable;
import com.ser.rest.response.ResponseError;
import com.ser.service.property.PropertiesService;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping(value = "/new")
public class NewProviderController {

    private OauthApiProviderRepository oauthApiProviderRepository;

    @RequestMapping(value = "/provider", method = RequestMethod.POST)
    public ResponseEntity<Responsable> createNewApiProvider(
            @RequestBody @Valid NewApiProviderRequest newApiProviderRequest
    ) {

        log.info("Received request: " + newApiProviderRequest);

        if (newApiProviderRequest.getAccessCode() == null || !newApiProviderRequest.getAccessCode()
                .equals(PropertiesService.CommonProperties.getAccessCode())) {

            log.error("Response sent: " + ResponseError.ACCESS_DENIED);
            return ResponseEntity.status(ResponseError.ACCESS_DENIED.getCode()).body(ResponseError.ACCESS_DENIED);
        }

        Optional<OauthApiProvider> optionalOauthApiProvider = oauthApiProviderRepository.findByProviderNameIgnoreCase(newApiProviderRequest.getProviderName());
        if (optionalOauthApiProvider.isPresent()) {

            log.error("Response sent: " + ResponseError.API_PROVIDER_EXISTS);
            return ResponseEntity.status(ResponseError.API_PROVIDER_EXISTS.getCode()).body(ResponseError.API_PROVIDER_EXISTS);
        }

        OauthApiProvider oauthApiProvider = new OauthApiProvider();
        oauthApiProvider.setProviderName(newApiProviderRequest.getProviderName());
        oauthApiProvider.setDefaultClientId(newApiProviderRequest.getDefaultClientId());
        oauthApiProvider.setDefaultClientSecret(newApiProviderRequest.getDefaultClientSecret());
        oauthApiProvider.setProviderCodeUrl(newApiProviderRequest.getProviderCodeUrl());
        oauthApiProvider.setProviderTokenFromCodeUrl(newApiProviderRequest.getProviderTokenFromCodeUrl());
        oauthApiProvider.setDefaultScope(newApiProviderRequest.getDefaultScope());

        oauthApiProviderRepository.save(oauthApiProvider);

        log.info("Response sent: " + ResponseError.API_PROVIDER_CREATED);
        return ResponseEntity.status(ResponseError.API_PROVIDER_CREATED.getCode()).body(ResponseError.API_PROVIDER_CREATED);

    }

    @Autowired
    public NewProviderController(OauthApiProviderRepository oauthApiProviderRepository) {
        this.oauthApiProviderRepository = oauthApiProviderRepository;
    }
}
