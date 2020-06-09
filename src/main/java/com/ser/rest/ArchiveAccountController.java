package com.ser.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ser.model.entity.OauthAccount;
import com.ser.model.entity.OauthApiProvider;
import com.ser.model.repository.OauthAccountRepository;
import com.ser.model.repository.OauthApiProviderRepository;
import com.ser.rest.request.ArchiveAccountRequest;
import com.ser.rest.response.Responsable;
import com.ser.rest.response.ResponseError;
import com.ser.service.property.PropertiesService;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping
public class ArchiveAccountController {

    private OauthApiProviderRepository oauthApiProviderRepository;
    private OauthAccountRepository oauthAccountRepository;

    @RequestMapping(value = "/archive/account", method = RequestMethod.POST)
    public ResponseEntity<Responsable> archiveAccount(@RequestBody @Valid ArchiveAccountRequest archiveAccountRequest) {

        log.info("Received request: " + archiveAccountRequest);

        Responsable response = isAccessCodeRight(archiveAccountRequest, true);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @RequestMapping(value = "/unarchive/account", method = RequestMethod.POST)
    public ResponseEntity<Responsable> unArchiveAccount(@RequestBody @Valid ArchiveAccountRequest archiveAccountRequest) {

        log.info("Received request: " + archiveAccountRequest);
        Responsable response = isAccessCodeRight(archiveAccountRequest, false);
        return ResponseEntity.status(response.getCode()).body(response);


    }

    private Responsable isAccessCodeRight(ArchiveAccountRequest archiveAccountRequest, boolean isArchive) {

        if (archiveAccountRequest.getAccessCode() == null || !archiveAccountRequest.getAccessCode()
                .equals(PropertiesService.CommonProperties.getAccessCode())) {

            log.error("Response sent: " + ResponseError.ACCESS_DENIED);
            return ResponseError.ACCESS_DENIED;
        }

        Optional<OauthApiProvider> optionalOauthApiProvider = oauthApiProviderRepository
                .findByProviderNameIgnoreCase(archiveAccountRequest.getApiProviderName());
        if (!optionalOauthApiProvider.isPresent()) {

            log.error("Response sent: " + ResponseError.API_PROVIDER_NOT_FOUND);
            return ResponseError.API_PROVIDER_NOT_FOUND;
        }

        Optional<OauthAccount> optionalOauthAccount = oauthAccountRepository
                .findByAccountNameIgnoreCaseAndOauthApiProviderId(
                        archiveAccountRequest.getAccountName(),
                        optionalOauthApiProvider.get().getId()
                );

        if (!optionalOauthAccount.isPresent()) {

            log.error("Response sent: " + ResponseError.ACCOUNT_NOT_FOUND);
            return ResponseError.ACCOUNT_NOT_FOUND;
        }
        OauthAccount oauthAccount = optionalOauthAccount.get();

        if (isArchive) {
            if (oauthAccount.getIsArchive()) {

                log.error("Response sent: " + ResponseError.ACCOUNT_ALREADY_ARCHIVED);
                return ResponseError.ACCOUNT_ALREADY_ARCHIVED;
            }

            oauthAccount.setIsArchive(true);
            oauthAccountRepository.save(oauthAccount);

            log.info("Response sent: " + ResponseError.ACCOUNT_ARCHIVED);
            return ResponseError.ACCOUNT_ARCHIVED;
        }

        if (!oauthAccount.getIsArchive()) {

            log.error("Response sent: " + ResponseError.ACCOUNT_ALREADY_UNARCHIVED);
            return ResponseError.ACCOUNT_ALREADY_UNARCHIVED;
        }

        oauthAccount.setIsArchive(false);
        oauthAccountRepository.save(oauthAccount);
        log.info("Response sent: " + ResponseError.ACCOUNT_UNARCHIVED);
        return ResponseError.ACCOUNT_UNARCHIVED;


    }

    public ArchiveAccountController(
            OauthApiProviderRepository oauthApiProviderRepository,
            OauthAccountRepository oauthAccountRepository

    ) {

        this.oauthAccountRepository = oauthAccountRepository;
        this.oauthApiProviderRepository = oauthApiProviderRepository;
    }
}
