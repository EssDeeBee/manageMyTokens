package com.ser.service.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.ser.model.ApiProvider;
import com.ser.model.entity.OauthAccount;
import com.ser.model.entity.OauthAccountToken;
import com.ser.model.repository.OauthAccountRepository;
import com.ser.model.repository.OauthAccountTokenRepository;
import com.ser.service.property.PropertiesService;
import com.ser.webutils.webclient.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Class used in cases when new token for an accountName is required.
 */
@Service
@Slf4j
public class TokenFromCode {

    private OauthAccountRepository oauthAccountRepository;
    private OauthAccountTokenRepository oauthAccountTokenRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    private RestClient restClient;

    /**
     * Async method, gets token for the accountName and save it in the repository.
     *
     * @param oauthAccount OauthAccount from the repository can't be null.
     * @return none
     */
    @Async
    public void getToken(OauthAccount oauthAccount, String codeForToken) {

        log.info("Building \"token from code\" request");

        String receiverControllerURI = "/oauth/code/receiver";
        String redirectURI = PropertiesService
                .CommonProperties
                .getHostAddress()
                + receiverControllerURI;


        List<NameValuePair> variables = new ArrayList<NameValuePair>() {
            {
                add(new BasicNameValuePair("grant_type", "authorization_code"));
                add(new BasicNameValuePair("code", codeForToken));
                add(new BasicNameValuePair("client_id", oauthAccount.getClientId()));
                add(new BasicNameValuePair("client_secret", oauthAccount.getClientSecret()));
                add(new BasicNameValuePair("redirect_uri", redirectURI));
            }
        };

        requestToken(oauthAccount, variables);
    }


    /**
     * Method requests token using code an other params
     *
     * @param oauthAccount OauthAccount from the repository can't be null
     * @param variables    variables for the token request
     */
    private void requestToken(OauthAccount oauthAccount, List<NameValuePair> variables) {

        String company = oauthAccount.getOauthApiProvider().getProviderName();
        String mediaTypeRequest = ApiProvider.findByName(company).getMediaType();

        String getTokenURL = oauthAccount.getOauthApiProvider().getProviderTokenFromCodeUrl();

        Optional<String> jsonResponse = restClient.sendTokenRequest(getTokenURL, variables, mediaTypeRequest);

        if (jsonResponse.isPresent()) {
            saveToken(oauthAccount, jsonResponse.get());
        } else {
            log.error("Failed to get a token");
        }
    }

    /**
     * Method saves received token in to the repository as an entity.
     * And releases accountName status"
     *
     * @param oauthAccount OauthAccount from the repository can't be null
     * @param jsonResponse response from token request
     */
    private void saveToken(OauthAccount oauthAccount, String jsonResponse) {

        try {

            log.info("Releasing accountName status and state");
            oauthAccount.setStatus("");
            oauthAccount.setState("");
            oauthAccount.setStateExpireDate(null);
            oauthAccountRepository.save(oauthAccount);

            log.info("Parsing json request into oauth token entity");
            OauthAccountToken oauthAccountToken = objectMapper.readValue(jsonResponse, OauthAccountToken.class);
            if (oauthAccountToken.getRefreshToken() == null) {
                oauthAccountToken.setRefreshToken("");
            }

            log.info("Saving the result");
            oauthAccountToken.setOauthAccount(oauthAccount);
            oauthAccountTokenRepository.save(oauthAccountToken);

            log.info("Received token is: " + oauthAccountToken.getAccessToken() + " for accountName " + oauthAccount);

        } catch (JsonParseException | IOException ex) {

            log.error("Error found while parsing json: " + jsonResponse + " accountName: " + oauthAccount);
            ex.printStackTrace();
        }
    }

    @Autowired
    public TokenFromCode(
            RestClient restClient,
            OauthAccountRepository oauthAccountRepository,
            OauthAccountTokenRepository oauthAccountTokenRepository
    ) {
        this.oauthAccountRepository = oauthAccountRepository;
        this.oauthAccountTokenRepository = oauthAccountTokenRepository;
        this.restClient = restClient;

    }
}
