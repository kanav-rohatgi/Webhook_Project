package com.webhook.webhookservice.controller;

import com.webhook.webhookservice.config.AppConstants;
import com.webhook.webhookservice.payload.EndpointDTO;
import com.webhook.webhookservice.payload.EndpointResponse;
import com.webhook.webhookservice.service.EndpointService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/endpoints")
public class EndpointController {

    private final EndpointService endpointService;

    public EndpointController(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    private String extractEmail(Authentication auth) {
        Jwt jwt = (Jwt) auth.getPrincipal();
        return jwt.getClaim("email");
    }

    @PostMapping
    public ResponseEntity<EndpointDTO> createEndpoint(@RequestBody EndpointDTO endpointDTO,
                                                       HttpServletRequest request,
                                                       Authentication authentication) {
        String email = extractEmail(authentication);
        EndpointDTO created = endpointService.createEndpoint(endpointDTO, request, email);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<EndpointResponse> getAllEndpoints(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_ENDPOINT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR) String sortOrder,
            HttpServletRequest request,
            Authentication authentication) {
        String email = extractEmail(authentication);
        EndpointResponse response = endpointService.getAllEndpoints(pageNumber, pageSize, sortBy, sortOrder, request, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{endpointId}")
    public ResponseEntity<EndpointDTO> getEndpointById(@PathVariable Long endpointId,
                                                        HttpServletRequest request,
                                                        Authentication authentication) {
        String email = extractEmail(authentication);
        return ResponseEntity.ok(endpointService.searchEndpointById(endpointId, request, email));
    }

    @GetMapping("/name/{endpointName}")
    public ResponseEntity<EndpointDTO> getEndpointByName(@PathVariable String endpointName,
                                                           HttpServletRequest request,
                                                           Authentication authentication) {
        String email = extractEmail(authentication);
        return ResponseEntity.ok(endpointService.searchEndpointByName(endpointName, request, email));
    }

    @DeleteMapping("/{endpointId}")
    public ResponseEntity<EndpointDTO> deleteEndpoint(@PathVariable Long endpointId,
                                                       HttpServletRequest request,
                                                       Authentication authentication) {
        String email = extractEmail(authentication);
        return ResponseEntity.ok(endpointService.deleteEndpoint(endpointId, request, email));
    }
}
