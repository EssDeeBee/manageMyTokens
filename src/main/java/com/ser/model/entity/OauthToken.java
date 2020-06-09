package com.ser.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class OauthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JsonProperty("access_token")
    @Column(nullable = false)
    private String accessToken;

    @JsonProperty("refresh_token")
    @Column(nullable = false)
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("tokens_left")
    private String tokensLeft;

    @JsonProperty("expires_in")
    @Column(nullable = false)
    private Long tokenExpiresIn;

    public void setTokenExpiresIn(Long tokenExpiresIn) {
        this.tokenExpiresIn = tokenExpiresIn;
        if (tokenExpiresIn != null)
            if (tokenExpiresIn == 0) {
                this.tokenExpiresDate = Timestamp.valueOf(LocalDateTime.now().plusYears(1L));
            } else {
                this.tokenExpiresDate = new Timestamp(new Date().getTime() + tokenExpiresIn * 1000L);
            }
    }

    @JsonIgnore
    @Column(nullable = false)
    private Timestamp tokenRefreshDate = new Timestamp(new Date().getTime());

    @JsonIgnore
    @Column(nullable = false)
    private Timestamp tokenExpiresDate;
}
