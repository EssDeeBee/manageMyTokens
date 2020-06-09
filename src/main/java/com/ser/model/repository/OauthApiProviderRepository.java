package com.ser.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ser.model.entity.OauthApiProvider;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OauthApiProviderRepository extends JpaRepository<OauthApiProvider, UUID> {

    Optional<OauthApiProvider> findByProviderNameIgnoreCase(String providerName);

}
