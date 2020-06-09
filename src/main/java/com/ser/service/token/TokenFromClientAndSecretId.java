package com.ser.service.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;
import com.ser.model.ApiProvider;
import com.ser.model.entity.OauthAccount;
import com.ser.model.entity.OauthAccountToken;
import com.ser.model.repository.OauthAccountTokenRepository;
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
public class TokenFromClientAndSecretId {

    private OauthAccountTokenRepository oauthAccountTokenRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    private RestClient restClient;

    /**
     * Async method, gets token for the accountName and save it in the repository.
     *
     * @param oauthAccount OauthAccount from the repository can't be null.
     * @return none
     */
    public Optional<String> getToken(OauthAccount oauthAccount) {

        log.info("Building \"token from client and secret id\" request");

        List<NameValuePair> variables = new ArrayList<NameValuePair>() {
            {
                add(new BasicNameValuePair("grant_type", "client_credentials"));
                add(new BasicNameValuePair("client_id", oauthAccount.getClientId()));
                add(new BasicNameValuePair("client_secret", oauthAccount.getClientSecret()));
            }
        };

        return requestToken(oauthAccount, variables);
    }


    /**
     * Method requests token using code an other params
     *
     * @param oauthAccount OauthAccount from the repository can't be null
     * @param variables    variables for the token request
     */
    private Optional<String> requestToken(OauthAccount oauthAccount, List<NameValuePair> variables) {

        String company = oauthAccount.getOauthApiProvider().getProviderName();
        String mediaTypeRequest = ApiProvider.findByName(company).getMediaType();

        String tokenURLRequest = oauthAccount.getOauthApiProvider().getProviderTokenFromCodeUrl();
        Optional<String> jsonResponse = restClient.sendTokenRequest(tokenURLRequest, variables, mediaTypeRequest);

        if (jsonResponse.isPresent()) {
            return saveTokenRequestAndReturnToken(oauthAccount, jsonResponse.get());
        } else {
            log.error("Failed to get a token");
        }
        return Optional.empty();
    }

    /**
     * Method saves received token in to the repository as an entity.
     * And releases accountName status"
     *
     * @param oauthAccount OauthAccount from the repository can't be null
     * @param jsonResponse response from token request
     */
    private Optional<String> saveTokenRequestAndReturnToken(OauthAccount oauthAccount, String jsonResponse) {

        try {

            log.info("Parsing json request into oauth token entity");
            OauthAccountToken oauthAccountToken = objectMapper.readValue(jsonResponse, OauthAccountToken.class);
            if (oauthAccountToken.getRefreshToken() == null) {
                oauthAccountToken.setRefreshToken("");
            }

            log.info("Saving the result");
            oauthAccountToken.setOauthAccount(oauthAccount);
            oauthAccountTokenRepository.save(oauthAccountToken);

            log.info("Received token is: " + oauthAccountToken.getAccessToken() + " for accountName " + oauthAccount);
            return Optional.of(oauthAccountToken.getAccessToken());

        } catch (JsonParseException | IOException ex) {
            log.error("Error found while parsing json: " + jsonResponse + " accountName: " + oauthAccount);
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    @Autowired
    public TokenFromClientAndSecretId(
            RestClient restClient,
            OauthAccountTokenRepository oauthAccountTokenRepository
    ) {
        this.oauthAccountTokenRepository = oauthAccountTokenRepository;
        this.restClient = restClient;

    }
}
