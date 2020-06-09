package com.ser.model;

import lombok.Getter;
import org.springframework.http.MediaType;

@Getter
public enum ApiProvider {

    GOOGLE(MediaType.APPLICATION_FORM_URLENCODED_VALUE, "&access_type=offline"),
    YANDEX(MediaType.APPLICATION_FORM_URLENCODED_VALUE, ""),
    MY_TARGET(MediaType.APPLICATION_FORM_URLENCODED_VALUE, ""),
    VKONTAKTE(MediaType.APPLICATION_FORM_URLENCODED_VALUE, ""),
    DEFAULT;

    private String mediaType;
    private String additionalHeaders;

    public static ApiProvider findByName(String name) {
        for (ApiProvider enumCheck : values()) {
            if (enumCheck.name().equalsIgnoreCase(name)) {
                return enumCheck;
            }
        }
        return DEFAULT;
    }

    ApiProvider(String mediaType, String additionalHeaders) {
        this.mediaType = mediaType;
        this.additionalHeaders = additionalHeaders;
    }

    ApiProvider() {
        this.mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE;
        this.additionalHeaders = "";
    }

}
