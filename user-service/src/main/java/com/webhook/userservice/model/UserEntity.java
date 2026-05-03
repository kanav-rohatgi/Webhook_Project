package com.webhook.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "W_USER")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id")
    private Long id;

    @Column(name = "u_email", unique = true, nullable = false)
    private String email;

    @Column(name = "u_is_email_verified")
    private Boolean isEmailVerified;

    @Column(name = "u_name")
    private String name;

    @Column(name = "u_given_name")
    private String givenName;

    @Column(name = "u_family_name")
    private String familyName;

    @Column(name = "u_preferred_username")
    private String preferredUsername;
}
