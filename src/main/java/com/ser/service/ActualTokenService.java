package com.ser.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ser.model.entity.*;
import com.ser.model.repository.*;
import com.ser.rest.response.Responsable;
import com.ser.rest.response.ResponseError;
import com.ser.rest.response.ResponseToken;
import com.ser.service.token.ClientsToken;
import com.ser.service.token.CodeForToken;
import com.ser.service.token.TokenFromClientAndSecretId;
import com.ser.service.token.TokenFromRefreshToken;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ActualTokenService {

    private OauthApiProviderRepository oauthApiProviderRepository;
    private OauthAccountRepository oauthAccountRepository;
    private OauthAccountTokenRepository oauthAccountTokenRepository;
    private OauthClientTokenRepository oauthClientTokenRepository;
    private OauthClientRepository oauthClientRepository;
    private TokenFromRefreshToken tokenFromRefreshToken;
    private TokenFromClientAndSecretId tokenFromClientAndSecretId;
    private CodeForToken codeForToken;
    private ClientsToken clientsToken;

    private static List<String> clientRequestCheckingList = Collections.synchronizedList(new ArrayList<>());


    /**
     * Main service method that tries to get an actual token by given response.
     *
     * @param apiProvider  Name of api provider not case sensitive.
     * @param account      Name of accountName which token is required for.
     * @param clientMailId Client id, if the token required for a client. Nullable, if null than the token will be returned for the given accountName.
     * @param scope        Scope of rights that must be requested. Nullable, if null default scope will be used.
     * @return Responsable.
     */
    public Responsable getToken(String apiProvider, String account, String clientMailId, String scope) {

        //Trying to find the api provider by the given name, if not found sending the error message.
        log.info("Trying to find the API provider: " + apiProvider);
        Optional<OauthApiProvider> optionalOauthServiceProvider = oauthApiProviderRepository.findByProviderNameIgnoreCase(apiProvider);

        if (!optionalOauthServiceProvider.isPresent()) {
            log.error("Given API provider not found in the data base: " + apiProvider);
            return ResponseError.API_PROVIDER_NOT_FOUND;
        }

        //Trying to find the accountName by the given name, if not found sending the error message.
        UUID companyId = optionalOauthServiceProvider.get().getId();
        Optional<OauthAccount> optionalOauthAccount = oauthAccountRepository.findByAccountNameIgnoreCaseAndOauthApiProviderId(
                account,
                companyId
        );

        log.info("Trying to find the accountName " + account);

        if (!optionalOauthAccount.isPresent()) {
            log.error("Given accountName not found in the data base: " + account);
            return ResponseError.ACCOUNT_NOT_FOUND;
        }

        if (optionalOauthAccount.get().getIsArchive()) {

            log.error("Given accountName archived, response sent: " + ResponseError.ACCOUNT_ARCHIVED_ERROR);
            return ResponseError.ACCOUNT_ARCHIVED_ERROR;
        }

        //Checking if accountName on getting code stage(when waiting for an user response). If true sending a message that the accountName is locked.
        if (optionalOauthAccount.get().getStatus() != null
                && !optionalOauthAccount.get().getStatus().equals("")
                && optionalOauthAccount.get().getStateExpireDate() != null
                && Timestamp.valueOf(LocalDateTime.now()).before(optionalOauthAccount.get().getStateExpireDate())
                && optionalOauthAccount.get().getState() != null) {
            log.info("Getting token is in process for the given accountName: " + account);
            return ResponseError.LOCKED;
        }

        //Checking if client mail id is null, if true getting token for the accountName.
        if (clientMailId == null) {

            //Checking if the accountName has tokens, if has sending the last actual token.
            Optional<List<OauthAccountToken>> optionalOauthTokens = oauthAccountTokenRepository.findByOauthAccountId(optionalOauthAccount.get()
                    .getId());

            if (optionalOauthTokens.isPresent()) {
                Optional<OauthAccountToken> optionalLastOauthToken = getLastToken(optionalOauthTokens.get());

                if (optionalLastOauthToken.isPresent()) {
                    if (isTokenActual(optionalLastOauthToken.get())) {
                        return new ResponseToken(optionalLastOauthToken.get().getAccessToken());
                    }

                    if (optionalLastOauthToken.get().getRefreshToken() != null
                            && optionalLastOauthToken.get().getRefreshToken().length() > 1) {

                        //Getting a new token using the refresh token, if the result is not null sending the token. Otherwise sending the error message.
                        Optional<String> optionalRefreshedAccountToken = tokenFromRefreshToken.getTokenForAccount(
                                optionalOauthAccount.get(),
                                optionalLastOauthToken.get().getRefreshToken()
                        );

                        if (!optionalRefreshedAccountToken.isPresent()) {
                            log.error("Couldn't get a token from the refresh token accountName: " + optionalOauthAccount.get());
                            return ResponseError.INTERNAL_ERROR;
                        }
                        return new ResponseToken(optionalRefreshedAccountToken.get());
                    } else {
                        //In cases when an accountName doesn't have any tokens or refresh tokens sending an email message in order to create one.
                        //The email massage sends in new thread then the "locked" message will be sent.
                        codeForToken.getCode(optionalOauthAccount.get());
                        return ResponseError.LOCKED;
                    }

                }
            } else {
                //In cases when an accountName doesn't have any tokens or refresh tokens sending an email message in order to create one.
                //The email massage sends in new thread then the "locked" message will be sent.
                codeForToken.getCode(optionalOauthAccount.get());
                return ResponseError.LOCKED;
            }
            //Checking if accountName on getting code stage(when waiting for an user response). If true sending a message that the accountName is locked.
            if (optionalOauthAccount.get().getResponseType().equals("client_credentials")) {
                Optional<String> optionalToken = tokenFromClientAndSecretId.getToken(optionalOauthAccount.get());
                if (!optionalToken.isPresent()) {
                    log.error("Couldn't get a token from the refresh token accountName: " + optionalOauthAccount.get());
                    return ResponseError.INTERNAL_ERROR;
                }
                return new ResponseToken(optionalToken.get());
            }
        }
        //If the client mail id is not null, trying to get a token for the client.
        //If client is not found sending a message.
        Optional<OauthClient> oauthClient = oauthClientRepository.findByClientMailIdIgnoreCaseAndOauthAccountId(
                clientMailId,
                optionalOauthAccount.get().getId()
        );

        if (!oauthClient.isPresent()) {

            log.error("Couldn't find a client by given clientMailId:" + clientMailId);
            if (clientRequestCheckingList.contains(clientMailId)) {

                log.error("Client is already on creation stage : " + clientMailId);
                return ResponseError.LOCKED;
            }

            log.info("Trying to create a new one:");
            clientRequestCheckingList.add(clientMailId);

            Optional<String> optionalToken = clientsToken.createNewClient(optionalOauthAccount.get(), clientMailId);
            clientRequestCheckingList.remove(clientMailId);

            return optionalToken.<Responsable>map(ResponseToken::new).orElse(ResponseError.CLIENT_NOT_FOUND);
        }

        //Getting tokens of the client, if not null sending the last actual token.
        Optional<List<OauthClientToken>> oauthClientTokens = oauthClientTokenRepository.findByOauthClientId(oauthClient.get().getId());

        if (oauthClientTokens.isPresent()) {
            Optional<OauthClientToken> oauthClientToken = getLastToken(oauthClientTokens.get());

            if (oauthClientToken.isPresent()) {

                if (isTokenActual(oauthClientToken.get())) {
                    return new ResponseToken(oauthClientToken.get().getAccessToken());
                }

                //If there is no actual token, getting a new one using refresh token. If the result is not null sending the token. Otherwise sending the error message.
                Optional<String> optionalRefreshedClientToken = tokenFromRefreshToken.getTokenForClient(
                        optionalOauthAccount.get(),
                        oauthClient.get(),
                        oauthClientToken.get().getRefreshToken()
                );
                if (!optionalRefreshedClientToken.isPresent()) {
                    log.error("Couldn't get a token from the refresh token accountName: " + optionalOauthAccount.get());
                    return ResponseError.INTERNAL_ERROR;
                }
                return new ResponseToken(optionalRefreshedClientToken.get());
            }
        }
        return ResponseError.INTERNAL_ERROR;
    }

    /**
     * Method gets a token that has expire date is the most recent.
     *
     * @param oauthTokens List of tokens.
     * @param <T>         Generic type. Should be extended from OauthToken.
     * @return Optional<T>
     */
    private <T extends OauthToken> Optional<T> getLastToken(List<T> oauthTokens) {
        Optional<T> optionalOauthToken = oauthTokens
                .stream()
                .max(Comparator.comparing(OauthToken::getTokenExpiresDate));

        if (!optionalOauthToken.isPresent()) {
            log.error("Couldn't find last token");
            return Optional.empty();
        }
        return optionalOauthToken;
    }

    /**
     * Method checks if the given token is actual, comparing token's expire date with current date minus 10 minutes.
     *
     * @param oauthToken token.
     * @param <T>        Generic type. Should be extended from OauthToken.
     * @return boolean.
     */
    private <T extends OauthToken> boolean isTokenActual(T oauthToken) {
        final long TEN_MINUTES = 600_000L;

        Long tokenExpiresTimestampMilliseconds = oauthToken
                .getTokenExpiresDate()
                .getTime();

        return tokenExpiresTimestampMilliseconds - TEN_MINUTES > System.currentTimeMillis();
    }

    @Autowired
    public ActualTokenService(
            CodeForToken codeForToken,
            OauthApiProviderRepository oauthApiProviderRepository,
            OauthAccountRepository oauthAccountRepository,
            OauthAccountTokenRepository oauthAccountTokenRepository,
            OauthClientTokenRepository oauthClientTokenRepository,
            OauthClientRepository oauthClientRepository,
            TokenFromRefreshToken tokenFromRefreshToken,
            ClientsToken clientsToken,
            TokenFromClientAndSecretId tokenFromClientAndSecretId
    ) {
        this.codeForToken = codeForToken;
        this.oauthApiProviderRepository = oauthApiProviderRepository;
        this.oauthAccountRepository = oauthAccountRepository;
        this.oauthAccountTokenRepository = oauthAccountTokenRepository;
        this.oauthClientTokenRepository = oauthClientTokenRepository;
        this.oauthClientRepository = oauthClientRepository;
        this.tokenFromRefreshToken = tokenFromRefreshToken;
        this.clientsToken = clientsToken;
        this.tokenFromClientAndSecretId = tokenFromClientAndSecretId;
    }
}
