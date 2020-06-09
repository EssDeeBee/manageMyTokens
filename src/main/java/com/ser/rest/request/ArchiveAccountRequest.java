package com.ser.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ArchiveAccountRequest {

    @NotEmpty(message = "account_name must not be empty")
    @JsonProperty(value = "account_name", required = true)
    private String accountName;

    @NotEmpty(message = "api_provider_name must not be empty")
    @JsonProperty(value = "api_provider_name")
    private String apiProviderName;

    @JsonProperty(value = "access_code", required = true)
    private String accessCode;

}
