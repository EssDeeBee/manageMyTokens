package com.ser.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TokenLink implements Responsable, Serializable {
    @Override
    @JsonIgnore
    public int getCode() {
        return 200;
    }

    @JsonProperty("token_link")
    private String tokenLink;
}
