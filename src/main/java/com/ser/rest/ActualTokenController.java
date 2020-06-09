package com.ser.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ser.rest.request.ActualTokenRequest;
import com.ser.rest.response.Responsable;
import com.ser.rest.response.ResponseError;
import com.ser.service.ActualTokenService;
import com.ser.service.property.PropertiesService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/oauth")
public class ActualTokenController {

    private ActualTokenService actualTokenService;

    /**
     * Main controller which returns the actual token for requested Account or client
     *
     * @param actualTokenRequest Request that serialises in class "ActualTokenRequest"
     * @return ResponseEntity
     */
    @RequestMapping(value = "/actual_token", method = RequestMethod.POST)
    public ResponseEntity<Responsable> getActualToken(
            @RequestBody @Valid ActualTokenRequest actualTokenRequest
    ) {

        if (actualTokenRequest.getAccessCode() != null && actualTokenRequest.getAccessCode()
                .equals(PropertiesService.CommonProperties.getAccessCode())) {

            log.info("Request received with following values apiProvider: " + actualTokenRequest);

            Responsable response = actualTokenService.getToken(
                    actualTokenRequest.getApiProvider(),
                    actualTokenRequest.getAccountName(),
                    actualTokenRequest.getClientMailId(),
                    actualTokenRequest.getScope()
            );

            log.info("Response sent: " + response);
            return ResponseEntity.status(response.getCode()).body(response);
        }
        log.error("Request declined, access_code is wrong: " + actualTokenRequest);
        return ResponseEntity.status(ResponseError.ACCESS_DENIED.getCode()).body(ResponseError.ACCESS_DENIED);
    }

    @Autowired
    public ActualTokenController(ActualTokenService actualTokenService) {
        this.actualTokenService = actualTokenService;

    }
}
