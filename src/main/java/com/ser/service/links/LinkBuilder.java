package com.ser.service.links;

import com.ser.model.ApiProvider;
import com.ser.model.entity.OauthAccount;

import java.util.UUID;


class LinkBuilder {

    private String scope;

    /**
     * Uri builder for getting code for token request
     *
     * @param oauthAccount OauthAccount
     * @param redirectURI  Redirect url will be used by an api provider to send a code into it.
     * @return String final built url.
     */
    static public String getNewTokenRequestLink(OauthAccount oauthAccount, String redirectURI) {

        StringBuilder finalUri = new StringBuilder();

        // Getting nullable params
        String scope = oauthAccount.getScope() == null || oauthAccount.getScope().length() < 1
                ? oauthAccount.getOauthApiProvider().getDefaultScope()
                : oauthAccount.getScope();

        String clientId = oauthAccount.getClientId() == null || oauthAccount.getClientId().length() < 1
                ? oauthAccount.getOauthApiProvider().getDefaultClientId()
                : oauthAccount.getClientId();

        String responseType = oauthAccount.getResponseType() == null || oauthAccount.getResponseType().length() < 1
                ? "code"
                : oauthAccount.getResponseType();

        //Appending main uri body for all api providers.
        finalUri.append(oauthAccount.getOauthApiProvider().getProviderCodeUrl())
                .append("?client_id=")
                .append(clientId)
                .append("&response_type=")
                .append(responseType)
                .append("&redirect_uri=")
                .append(redirectURI)
                .append("&login_hint=")
                .append(oauthAccount.getAccountName())
                .append("&state=")
                .append(oauthAccount.getState());

        // Checking the scope if exists appending it into the url string
        if (scope != null && scope.length() > 1) {
            finalUri
                    .append("&scope=")
                    .append(scope);
        }

        //Adding headers if the application was launched without the host_address property.
        String providerName = oauthAccount.getOauthApiProvider().getProviderName().toLowerCase();
        String hostMask = redirectURI.substring(0, redirectURI.indexOf('.')).replaceAll("^https?://", "");

        if (hostMask.equals("10") || hostMask.equals("192") || hostMask.equals("172")) {
            String deviceId = UUID.randomUUID().toString();
            String deviceName = providerName + "_token_receiver_service";
            finalUri
                    .append("&device_id=")
                    .append(deviceId)
                    .append("&device_name=")
                    .append(deviceName);
        }

        // Getting additional headers. If exists appending it into the string.
        String additionalHeaders = ApiProvider
                .findByName(oauthAccount.getOauthApiProvider().getProviderName())
                .getAdditionalHeaders();

        if (additionalHeaders != null && additionalHeaders.length() > 1) {
            finalUri
                    .append(additionalHeaders);
        }

        return finalUri.toString();
    }
}
