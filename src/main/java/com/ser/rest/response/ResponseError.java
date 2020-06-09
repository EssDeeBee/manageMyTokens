package com.ser.rest.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResponseError implements Responsable, Serializable {

    API_PROVIDER_CREATED(200, "ApiProvider created"),
    ACCOUNT_CREATED(200, "Account created"),
    ACCOUNT_ARCHIVED(200, "Account successfully archived"),
    ACCOUNT_UNARCHIVED(200, "Account successfully unarchived"),
    API_PROVIDER_NOT_FOUND(404, "Api provider not found"),
    API_PROVIDER_EXISTS(400, "ApiProvider already exists"),
    ACCOUNT_NOT_FOUND(404, "Account not found"),
    ACCOUNT_EXISTS(400, "Account already exists"),
    ACCOUNT_EXISTS_BUT_ARCHIVED(400, "Account already exists, but archived"),
    ACCOUNT_ARCHIVED_ERROR(400, "Account archived"),
    ACCOUNT_ALREADY_ARCHIVED(400, "Account already archived"),
    ACCOUNT_ALREADY_UNARCHIVED(400, "Account already unarchived"),
    CLIENT_NOT_FOUND(404, "Client not found"),
    ACCESS_DENIED(403, "Access denied"),
    REQUESTED_ACCOUNT_EXISTS(400, "Requested accountName already exists"),
    REQUESTED_CLIENT_EXISTS(400, "Requested client already exists"),
    REQUIRED_PARAM_MISSING(400, "One or more of required params are missing!"),
    REQUIRED_PARAM_ACCOUNT_MISSING(400, "Required param is missing: accountName"),
    REQUIRED_PARAM_PROVIDER_MISSING(400, "Required param is missing: api_provider"),
    REQUIRED_PARAM_CODE_MISSING(400, "Required param is missing: access_code"),
    INTERNAL_ERROR(500, "Internal Server Error"),
    LOCKED(423, "Locked");

    private int code;
    private String message;

    ResponseError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
