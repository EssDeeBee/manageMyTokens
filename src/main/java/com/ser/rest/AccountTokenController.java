package com.ser.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ser.rest.request.ActualTokenRequest;
import com.ser.rest.response.Responsable;
import com.ser.service.token.TokenLinkService;

import javax.validation.Valid;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/oauth/account")
public class AccountTokenController {

    private TokenLinkService tokenLinkService;

    /**
     * Controller for creating a token link
     *
     * @param actualTokenRequest actualTokenRequest
     * @return ResponseEntity<?>
     * @see ActualTokenRequest
     * @see ResponseEntity
     * @since 2020-04-15
     */
    @RequestMapping(value = "/token/link", method = RequestMethod.POST)
    public ResponseEntity<Responsable> getTokenLink(@RequestBody @Valid ActualTokenRequest actualTokenRequest) {

        log.info("Create token link  request has been received: {}", actualTokenRequest);
        Responsable responsable = tokenLinkService.generateTokenLink(actualTokenRequest);

        log.info("Token link sent to the client.");
        return ResponseEntity.status(responsable.getCode()).body(responsable);
    }
}
