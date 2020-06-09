package com.ser.rest.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response implements Responsable {

    private int code;
    private String message;

    @Override
    public int getCode() {
        return this.code;
    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
