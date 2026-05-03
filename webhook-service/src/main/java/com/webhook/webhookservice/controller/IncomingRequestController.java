package com.webhook.webhookservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webhook.webhookservice.config.AppConstants;
import com.webhook.webhookservice.payload.APIResponse;
import com.webhook.webhookservice.payload.IncomingRequestDTO;
import com.webhook.webhookservice.payload.IncomingRequestResponse;
import com.webhook.webhookservice.service.IncomingRequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class IncomingRequestController {

    private final IncomingRequestService incomingRequestService;

    public IncomingRequestController(IncomingRequestService incomingRequestService) {
        this.incomingRequestService = incomingRequestService;
    }

    private String extractEmail(Authentication auth) {
        Jwt jwt = (Jwt) auth.getPrincipal();
        return jwt.getClaim("email");
    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            Collections.list(headerNames).forEach(name -> headers.put(name, request.getHeader(name)));
        }
        return headers;
    }

    // ---- Catch-all: handles GET, POST, PUT, DELETE, PATCH on /{endpointName} ----
    @RequestMapping("/{endpointName}")
    public ResponseEntity<IncomingRequestDTO> handleIncomingRequest(
            @PathVariable String endpointName,
            @RequestBody(required = false) String body,
            HttpServletRequest request,
            Authentication authentication) throws JsonProcessingException {
        String email = extractEmail(authentication);
        Map<String, String> headers = extractHeaders(request);
        IncomingRequestDTO dto = incomingRequestService.handleIncomingRequest(
                endpointName, request, body, headers, email);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // ---- Get all requests for a named endpoint ----
    @GetMapping("/{endpointName}/requests")
    public ResponseEntity<IncomingRequestResponse> getRequestsByEndpointName(
            @PathVariable String endpointName,
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_INCOMINGREQUEST_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR) String sortOrder,
            HttpServletRequest request,
            Authentication authentication) {
        String email = extractEmail(authentication);
        IncomingRequestResponse response = incomingRequestService.getIncomingRequestsByEndpointName(
                pageNumber, pageSize, sortBy, sortOrder, request, endpointName, email);
        return ResponseEntity.ok(response);
    }

    // ---- Get single request ----
    @GetMapping("/{endpointName}/requests/{requestId}")
    public ResponseEntity<IncomingRequestDTO> getSingleRequest(
            @PathVariable String endpointName,
            @PathVariable Long requestId,
            HttpServletRequest request,
            Authentication authentication) {
        String email = extractEmail(authentication);
        return ResponseEntity.ok(
                incomingRequestService.getSingleRequestForEndpoint(request, endpointName, requestId, email));
    }

    // ---- Delete all requests for an endpoint ----
    @DeleteMapping("/{endpointName}/requests")
    public ResponseEntity<APIResponse> deleteAllRequests(
            @PathVariable String endpointName,
            HttpServletRequest request,
            Authentication authentication) {
        String email = extractEmail(authentication);
        incomingRequestService.deleteAllRequestsForEndpoint(endpointName, request, email);
        return ResponseEntity.ok(new APIResponse("All requests deleted for endpoint: " + endpointName, true));
    }

    // ---- Delete single request ----
    @DeleteMapping("/{endpointName}/requests/{requestId}")
    public ResponseEntity<IncomingRequestDTO> deleteSingleRequest(
            @PathVariable String endpointName,
            @PathVariable Long requestId,
            HttpServletRequest request,
            Authentication authentication) {
        String email = extractEmail(authentication);
        return ResponseEntity.ok(
                incomingRequestService.deleteRequestForEndpoint(endpointName, requestId, request, email));
    }
}
