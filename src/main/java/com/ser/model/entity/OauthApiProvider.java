package com.ser.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"oauthAccounts"})
@ToString(exclude = {"oauthAccounts"})
@Entity
@Table(name = "oauth_api_provider")
public class OauthApiProvider implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String providerName;

    @Column(nullable = false)
    private String defaultClientId;

    @Column(nullable = false)
    private String defaultClientSecret;

    @Column(nullable = false)
    private String providerCodeUrl;

    @Column(nullable = false)
    private String providerTokenFromCodeUrl;

    @Column(nullable = false)
    private String defaultScope;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "oauth_api_provider_id")
    Set<OauthAccount> oauthAccounts;

}
