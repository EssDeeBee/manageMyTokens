package com.ser.webutils.webclient.rest;

import lombok.Getter;
import org.springframework.http.MediaType;

@Getter
public enum EnumCheck {
    GOOGLE(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
    YANDEX(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
    DEFAULT;


    private String mediaType;

    static EnumCheck findByName(String name) {
        for (EnumCheck enumCheck : values()) {
            if (enumCheck.name().equalsIgnoreCase(name)) {
                return enumCheck;
            }
        }
        return DEFAULT;
    }

    EnumCheck(String mediaType) {
        this.mediaType = mediaType;
    }

    EnumCheck() {
        this.mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE;
    }

}
