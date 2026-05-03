package com.webhook.webhookservice.repository;

import com.webhook.webhookservice.model.EndpointEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EndpointRepository extends JpaRepository<EndpointEntity, Long> {

    Optional<EndpointEntity> findByEndpointName(String endpointName);

    Page<EndpointEntity> findByUserEmail(String userEmail, Pageable pageable);

    Optional<EndpointEntity> findByEndpointNameAndUserEmail(String endpointName, String userEmail);
}
