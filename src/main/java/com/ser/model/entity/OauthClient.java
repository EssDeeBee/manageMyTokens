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
@EqualsAndHashCode(exclude = {"oauthAccount", "oauthClientTokens"})
@ToString(exclude = {"oauthAccount", "oauthClientTokens"})
@Entity
@Table(name = "oauth_client", uniqueConstraints = @UniqueConstraint(columnNames = {"oauth_account_id", "client_mail_id"}))
public class OauthClient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, name = "client_mail_id")
    private String clientMailId;

    private String clientName;

    @JsonIgnore
    @ManyToOne(optional = false, cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    private OauthAccount oauthAccount;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "oauth_client_id")
    Set<OauthClientToken> oauthClientTokens;

}
