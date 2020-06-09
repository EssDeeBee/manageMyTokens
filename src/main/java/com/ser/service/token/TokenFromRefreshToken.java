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
import com.ser.model.entity.OauthClient;
import com.ser.model.entity.OauthClientToken;
import com.ser.model.repository.OauthAccountTokenRepository;
import com.ser.model.repository.OauthClientTokenRepository;
import com.ser.webutils.webclient.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TokenFromRefreshToken {

    private RestClient restClient;
    private ObjectMapper objectMapper = new ObjectMapper();
    private OauthAccountTokenRepository oauthAccountTokenRepository;
    private OauthClientTokenRepository oauthClientTokenRepository;

    /**
     * Method gets token from refresh token for the given accountName.
     * Token will be save in the database.
     *
     * @param oauthAccount - oauth accountName from the repository.
     * @param refreshToken - refresh token, for update the token.
     * @return String - token @nullable.
     */
    public Optional<String> getTokenForAccount(OauthAccount oauthAccount, String refreshToken) {
        Optional<String> jsonResponse = getJsonTokenResponse(oauthAccount, refreshToken);

        log.info("Sending token request for accountName: " + oauthAccount + " refresh token: " + refreshToken);
        return jsonResponse.map(s -> saveAccountToken(oauthAccount, s, refreshToken));
    }

    /**
     * Method gets token from refresh token for the given client.
     * Token will be save in the database.
     *
     * @param oauthAccount - oauth accountName from the repository.
     * @param oauthClient  - oauth client from the repository.
     * @param refreshToken - refresh token, for update the token.
     * @return String - token @nullable.
     */
    public Optional<String> getTokenForClient(OauthAccount oauthAccount, OauthClient oauthClient, String refreshToken) {
        Optional<String> jsonResponse = getJsonTokenResponse(oauthAccount, refreshToken);

        log.info("Sending token request for accountName: " + oauthAccount + " and client: " + oauthClient + "/n refresh token " + refreshToken);
        return jsonResponse.map(s -> saveClientToken(oauthClient, s, refreshToken));

    }

    /**
     * @param oauthAccount oauth accountName from the repository
     * @param refreshToken refresh token, for update the token.
     * @return Optional<String> json response with token
     */
    private Optional<String> getJsonTokenResponse(OauthAccount oauthAccount, String refreshToken) {
        String company = oauthAccount.getOauthApiProvider().getProviderName();
        String clientId = oauthAccount.getClientId();
        String clientSecret = oauthAccount.getClientSecret();
        String tokenFromCodeUrl = oauthAccount.getOauthApiProvider().getProviderTokenFromCodeUrl();


        return sendTokenRequest(
                company,
                clientId,
                clientSecret,
                refreshToken,
                tokenFromCodeUrl
        );
    }

    private Optional<String> sendTokenRequest(
            String company,
            String clientId,
            String secretId,
            String refreshToken,
            String tokenFromCodeUrl
    ) {
        String mediaTypeRequest = ApiProvider.findByName(company).getMediaType();

        List<NameValuePair> variables = new ArrayList<NameValuePair>() {
            {
                add(new BasicNameValuePair("grant_type", "refresh_token"));
                add(new BasicNameValuePair("refresh_token", refreshToken));
                add(new BasicNameValuePair("client_id", clientId));
                add(new BasicNameValuePair("client_secret", secretId));
            }
        };

        return restClient.sendTokenRequest(tokenFromCodeUrl, variables, mediaTypeRequest);

    }

    private String saveAccountToken(OauthAccount oauthAccount, String jsonResponse, String refreshToken) {

        try {
            log.info("Parsing json request into oauth token entity");
            OauthAccountToken oauthAccountToken = objectMapper.readValue(jsonResponse, OauthAccountToken.class);

            log.info("Saving the result");
            oauthAccountToken.setOauthAccount(oauthAccount);
            oauthAccountToken.setRefreshToken(refreshToken);

            oauthAccountTokenRepository.save(oauthAccountToken);

            log.info("Received token is: " + oauthAccountToken.getAccessToken() + " for accountName " + oauthAccount);

            return oauthAccountToken.getAccessToken();

        } catch (JsonParseException | IOException ex) {
            log.error("Error found while parsing json: " + jsonResponse + " accountName: " + oauthAccount);
            ex.printStackTrace();
        }
        return null;
    }

    private String saveClientToken(OauthClient oauthClient, String jsonResponse, String refreshToken) {

        try {
            log.info("Parsing json request into oauth token entity");
            OauthClientToken oauthClientToken = objectMapper.readValue(jsonResponse, OauthClientToken.class);

            log.info("Saving the result");
            oauthClientToken.setOauthClient(oauthClient);
            if (oauthClientToken.getRefreshToken() == null) {
                oauthClientToken.setRefreshToken(refreshToken);
            }

            oauthClientTokenRepository.save(oauthClientToken);

            log.info("Received token is: " + oauthClientToken.getAccessToken() + " for client " + oauthClient);

            return oauthClientToken.getAccessToken();

        } catch (JsonParseException | IOException ex) {
            log.error("Error found while parsing json: " + jsonResponse + " client: " + oauthClient);
            ex.printStackTrace();
        }

        return null;
    }

    @Autowired
    public TokenFromRefreshToken(
            RestClient restClient,
            OauthAccountTokenRepository oauthAccountTokenRepository,
            OauthClientTokenRepository oauthClientTokenRepository
    ) {
        this.restClient = restClient;
        this.oauthAccountTokenRepository = oauthAccountTokenRepository;
        this.oauthClientTokenRepository = oauthClientTokenRepository;

    }

}
