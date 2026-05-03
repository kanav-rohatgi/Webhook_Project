package com.webhook.webhookservice.repository;

import com.webhook.webhookservice.model.EndpointEntity;
import com.webhook.webhookservice.model.IncomingRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomingRequestRepository extends JpaRepository<IncomingRequestEntity, Long> {

    Page<IncomingRequestEntity> findByEndpointOrderByReceivedAtAsc(EndpointEntity endpoint, Pageable pageable);
}
