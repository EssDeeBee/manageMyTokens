package com.ser.initiation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PostNewAccountRequest implements Serializable {

    @JsonProperty(value = "api_provider", required = true)
    private String apiProvider;

    @JsonProperty(value = "account_name", required = true)
    private String accountName;

    @JsonProperty(value = "client_id", required = true)
    private String clientId;

    @JsonProperty(value = "client_secret", required = true)
    private String clientSecret;

    @JsonProperty(value = "access_token", required = true)
    private String accessToken;

    @JsonProperty(value = "refresh_token", required = true)
    private String refreshToken;

    @JsonProperty(value = "scope")
    private String scope;

    @JsonProperty(value = "state")
    private String state;

    @JsonProperty(value = "response_type")
    private String responseType;

    @JsonProperty(value = "status")
    private String status;
}
