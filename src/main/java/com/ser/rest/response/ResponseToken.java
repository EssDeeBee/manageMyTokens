package com.ser.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseToken implements Responsable, Serializable {
    private String token;

    @Override
    @JsonIgnore
    public int getCode() {
        return 200;
    }

    public ResponseToken(String token) {
        this.token = token;
    }
}
