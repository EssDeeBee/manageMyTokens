package com.ser.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ser.model.entity.OauthAccountToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OauthAccountTokenRepository extends JpaRepository<OauthAccountToken, UUID> {

    Optional<List<OauthAccountToken>> findByOauthAccountId(UUID oauthAccountId);

}
