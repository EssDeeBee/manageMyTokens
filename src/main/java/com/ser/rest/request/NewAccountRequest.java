package com.ser.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class NewAccountRequest {


    @NotEmpty(message = "account_name must not be empty")
    @JsonProperty(value = "account_name", required = true)
    private String accountName;

    @NotEmpty(message = "api_provider_name must not be empty")
    @JsonProperty(value = "api_provider_name")
    private String apiProviderName;

    @JsonProperty(value = "client_id")
    private String clientId;

    @JsonProperty(value = "client_secret")
    private String clientSecret;

    @JsonProperty(value = "response_type")
    private String responseType = "code";

    public void setResponseType(String responseType) {
        if (responseType != null) {
            this.responseType = responseType;
        }
    }

    @JsonProperty(value = "scope")
    private String scope = "";

    public void setScope(String scope) {
        if (scope != null) {
            this.scope = scope;
        }
    }

    @JsonProperty(value = "access_code", required = true)
    private String accessCode;
}
