package com.ser.service.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ser.model.ApiProvider;
import com.ser.model.entity.OauthAccount;
import com.ser.model.entity.OauthClient;
import com.ser.model.entity.OauthClientToken;
import com.ser.model.repository.OauthClientRepository;
import com.ser.model.repository.OauthClientTokenRepository;
import com.ser.webutils.webclient.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class ClientsToken {

    private OauthClientRepository oauthClientRepository;
    private OauthClientTokenRepository oauthClientTokenRepository;

    public Optional<String> createNewClient(OauthAccount oauthAccount, String clientMailId) {

        ObjectMapper objectMapper = new ObjectMapper();
        RestClient restClient = new RestClient();

        log.info("Building a request for get the token ");
        String mediaTypeRequest = ApiProvider.findByName(oauthAccount.getOauthApiProvider().getProviderName()).getMediaType();

        List<NameValuePair> variables = new ArrayList<NameValuePair>() {
            {
                add(new BasicNameValuePair("grant_type", "agency_client_credentials"));
                add(new BasicNameValuePair("agency_client_name", clientMailId));
                add(new BasicNameValuePair("client_id", oauthAccount.getClientId()));
                add(new BasicNameValuePair("client_secret", oauthAccount.getClientSecret()));
            }
        };

        Optional<String> optionalJsonResponse = restClient
                .sendTokenRequest(oauthAccount.getOauthApiProvider().getProviderTokenFromCodeUrl(), variables, mediaTypeRequest);
        if (optionalJsonResponse.isPresent()) {
            try {
                OauthClientToken oauthClientToken = objectMapper.readValue(optionalJsonResponse.get(), OauthClientToken.class);

                OauthClient oauthClient = new OauthClient();
                oauthClient.setClientMailId(clientMailId);
                oauthClient.setClientName("");
                oauthClient.setOauthAccount(oauthAccount);
                oauthClientRepository.save(oauthClient);

                oauthClientToken.setOauthClient(oauthClient);
                oauthClientTokenRepository.save(oauthClientToken);
                return Optional.of(oauthClientToken.getAccessToken());

            } catch (IOException ex) {
                log.error("Error found while parsing json: "
                        + optionalJsonResponse.get()
                        + " accountName: "
                        + oauthAccount
                        + " client: "
                        + clientMailId);

                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    @Autowired
    public ClientsToken(
            OauthClientRepository oauthClientRepository,
            OauthClientTokenRepository oauthClientTokenRepository
    ) {
        this.oauthClientTokenRepository = oauthClientTokenRepository;
        this.oauthClientRepository = oauthClientRepository;
    }
}
