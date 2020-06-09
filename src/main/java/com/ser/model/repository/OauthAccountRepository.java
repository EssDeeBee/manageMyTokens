package com.ser.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ser.model.entity.OauthAccount;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OauthAccountRepository extends JpaRepository<OauthAccount, UUID> {

    Optional<OauthAccount> findByAccountNameIgnoreCaseAndOauthApiProviderId(String accountName, UUID oauthServiceProviderId);

    Optional<OauthAccount> findByState(String state);

}
