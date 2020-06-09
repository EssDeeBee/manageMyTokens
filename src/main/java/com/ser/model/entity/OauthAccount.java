package com.ser.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"oauthApiProvider", "oauthAccountTokens", "scope", "status"})
@ToString(exclude = {"oauthApiProvider", "oauthAccountTokens"})
@Entity
@Table(name = "oauth_account", uniqueConstraints = @UniqueConstraint(columnNames = {"oauth_api_provider_id", "account_name"}))
public class OauthAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, name = "account_name")
    private String accountName;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String clientSecret;

    private String scope;

    private String state;

    private Timestamp stateExpireDate;

    private String responseType;

    private String status;

    @Column(nullable = false)
    private Boolean isArchive = false;

    public void setIsArchive(Boolean isArchive) {
        if (isArchive != null) {
            this.isArchive = isArchive;
        }
    }

    @JsonIgnore
    @ManyToOne(optional = false, cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    private OauthApiProvider oauthApiProvider;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "oauth_account_id")
    Set<OauthAccountToken> oauthAccountTokens;

}
