package com.ser.service.token;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.ser.model.entity.OauthAccount;
import com.ser.model.entity.OauthApiProvider;
import com.ser.model.repository.OauthAccountRepository;
import com.ser.model.repository.OauthApiProviderRepository;
import com.ser.rest.request.ActualTokenRequest;
import com.ser.rest.response.Responsable;
import com.ser.rest.response.ResponseError;
import com.ser.rest.response.TokenLink;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TokenLinkService {

    private OauthApiProviderRepository oauthApiProviderRepository;
    private OauthAccountRepository oauthAccountRepository;
    private CodeForToken codeForToken;

    /**
     * Processes given token request and creates a token link.
     *
     * @param actualTokenRequest ActualTokenRequest
     * @return Responsable
     * @see TokenLink
     * @see Responsable
     * @since 2020-04-15
     */
    public Responsable generateTokenLink(ActualTokenRequest actualTokenRequest) {


        Optional<OauthApiProvider> optionalOauthApiProvider = oauthApiProviderRepository.findByProviderNameIgnoreCase(actualTokenRequest.getApiProvider());
        if (optionalOauthApiProvider.isPresent()) {
            Optional<OauthAccount> optionalOauthAccount = oauthAccountRepository
                    .findByAccountNameIgnoreCaseAndOauthApiProviderId(actualTokenRequest.getAccountName(), optionalOauthApiProvider.get().getId());
            if (optionalOauthAccount.isPresent()) {
                String tokenLink = codeForToken.registerAndGetTokenLink(optionalOauthAccount.get());
                return new TokenLink(tokenLink);
            }
            log.warn("Given account bot found: {}", optionalOauthAccount);
            return ResponseError.ACCOUNT_NOT_FOUND;
        }
        log.warn("Given account bot found: {}", optionalOauthApiProvider);
        return ResponseError.API_PROVIDER_NOT_FOUND;
    }

}
