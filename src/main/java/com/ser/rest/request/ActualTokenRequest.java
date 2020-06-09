package com.ser.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ActualTokenRequest {

    @NotEmpty(message = "account_name must not be empty")
    @JsonProperty(value = "account_name", required = true)
    private String accountName;

    @NotEmpty(message = "api_provider must not be empty")
    @JsonProperty(value = "api_provider", required = true)
    private String ApiProvider;

    @JsonProperty(value = "access_code", required = true)
    private String accessCode;

    @JsonProperty(value = "scope")
    private String scope;

    @JsonProperty(value = "client_mail_id")
    private String clientMailId;

}
