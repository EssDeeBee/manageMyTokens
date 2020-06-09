package com.ser.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ser.model.entity.OauthClientToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OauthClientTokenRepository extends JpaRepository<OauthClientToken, UUID> {

    Optional<List<OauthClientToken>> findByOauthClientId(UUID oauthClientId);
}
