package com.ser.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ser.model.entity.OauthClient;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OauthClientRepository extends JpaRepository<OauthClient, UUID> {

    Optional<OauthClient> findByClientMailIdIgnoreCaseAndOauthAccountId(String mailId, UUID oauthAccountId);

}
