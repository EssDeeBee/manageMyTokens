package com.ser.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class NewApiProviderRequest {

    @NotEmpty(message = "provider_name must not be empty")
    @JsonProperty(value = "provider_name")
    private String providerName;

    @NotEmpty(message = "provider_code_url must not be empty")
    @JsonProperty(value = "provider_code_url")
    private String providerCodeUrl;

    @NotEmpty(message = "provider_token_from_code_url must not be empty")
    @JsonProperty(value = "provider_token_from_code_url")
    private String providerTokenFromCodeUrl;

    @NotEmpty(message = "default_client_id must not be empty")
    @JsonProperty(value = "default_client_id")
    private String defaultClientId;

    @NotEmpty(message = "default_secret_id must not be empty")
    @JsonProperty(value = "default_client_secret")
    private String defaultClientSecret;

    @JsonProperty(value = "default_scope")
    private String defaultScope = "";

    public void setDefaultScope(String defaultScope) {
        if (defaultScope != null) {
            this.defaultScope = defaultScope;
        }
    }

    @JsonProperty(value = "access_code")
    private String accessCode;
}
