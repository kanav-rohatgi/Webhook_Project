package com.webhook.webhookservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "W_ENDPOINT")
@Getter
@Setter
@NoArgsConstructor
public class EndpointEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "e_endpoint_id")
    private Long endpointId;

    @Column(name = "e_endpoint_name", unique = true, nullable = false)
    private String endpointName;

    @Column(name = "e_description")
    private String description;

    /**
     * Replaces the @ManyToOne UserEntity FK.
     * In a microservices world, we store the owner's email only.
     * The User Service owns the user record; we never join across services.
     */
    @Column(name = "e_user_email", nullable = false)
    private String userEmail;

    @OneToMany(mappedBy = "endpoint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncomingRequestEntity> incomingRequests = new ArrayList<>();
}
