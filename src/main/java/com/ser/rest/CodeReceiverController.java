package com.ser.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ser.model.entity.OauthAccount;
import com.ser.model.repository.OauthAccountRepository;
import com.ser.service.token.CodeForToken;
import com.ser.service.token.TokenFromCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;


@Slf4j
@RestController
@RequestMapping(value = "/oauth/code")
public class CodeReceiverController {

    private OauthAccountRepository oauthAccountRepository;
    private CodeForToken codeForToken;
    private TokenFromCode tokenFromCode;
    private static final String HTML_PAGE_FOR_SHOW = "config/show_page.html";

    /**
     * Controller gets and handle a response from api provider when a user clicks and grants rights to the application.
     *
     * @param code  various string that sends by the api provider in order to get a token from it.
     * @param state generated value by the application in order to connect the response with the specified accountName.
     * @return ResponseEntity.
     */
    @RequestMapping(value = "/receiver", method = RequestMethod.GET)
    public ResponseEntity redirectHandler(@RequestParam String code, @RequestParam String state) {

        log.info("Received code: " + code + ", received state: " + state);

        // Checking received variables, that the are not null.
        if (state == null) {
            return ResponseEntity.badRequest().body("Required param is missing: state");
        }
        if (code == null) {
            return ResponseEntity.badRequest().body("Required param is missing: code");
        }

        // Getting accountName by given state, if not exist sending error response:
        Optional<OauthAccount> optionalOauthAccount = oauthAccountRepository.findByState(state);
        if (!optionalOauthAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account hasn't found by given state");
        }

        // Checking if the current date is less than state expire date,
        // if false sending a new email in parallel thread and then sending a response that the state is expired.
        if (optionalOauthAccount.get().getStateExpireDate() == null
                || Timestamp.valueOf(LocalDateTime.now()).after(optionalOauthAccount.get().getStateExpireDate())
        ) {

            codeForToken.getCode(optionalOauthAccount.get());
            return ResponseEntity.badRequest().body("State is expired new email created and sent");
        }

        //If everything is okay, than getting a token by received code.
        tokenFromCode.getToken(optionalOauthAccount.get(), code);
        return ResponseEntity.ok().body(getHtmlPageForShow());
    }

    private String getHtmlPageForShow() {
        File file = new File(HTML_PAGE_FOR_SHOW);
        if (file.exists() && file.isFile()) {
            {
                try (Scanner scanner = new Scanner(file)) {
                    StringBuilder stringBuilder = new StringBuilder();

                    while (scanner.hasNext()) {
                        stringBuilder.append(scanner.nextLine());
                    }
                    System.out.println(stringBuilder.toString());
                    return stringBuilder.toString();
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else {
            log.info("Could not find the html file for sending it as a last show page: {}", file);
            log.info("Default page has sent instead.");
            return "<h1>Thank you!</h1><br/><h3>Code successfully received, you may close this window now.</h3>";
        }
    }

    @Autowired
    CodeReceiverController(
            OauthAccountRepository oauthAccountRepository,
            CodeForToken codeForToken,
            TokenFromCode tokenFromCode
    ) {

        this.tokenFromCode = tokenFromCode;
        this.codeForToken = codeForToken;
        this.oauthAccountRepository = oauthAccountRepository;

    }
}
