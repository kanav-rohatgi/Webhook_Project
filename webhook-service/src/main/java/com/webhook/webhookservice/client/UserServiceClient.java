package com.webhook.webhookservice.client;

import com.webhook.webhookservice.payload.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Calls user-service via Eureka service discovery (lb://user-service).
 * Used to verify that the email from the Google JWT actually exists in user-service.
 */
@FeignClient(name = "user-service", path = "/users")
public interface UserServiceClient {

    @GetMapping("/internal/{email}")
    UserResponse getUserByEmail(@PathVariable("email") String email);
}
