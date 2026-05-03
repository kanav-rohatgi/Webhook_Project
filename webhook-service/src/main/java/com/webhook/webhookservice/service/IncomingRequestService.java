package com.webhook.webhookservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webhook.webhookservice.payload.IncomingRequestDTO;
import com.webhook.webhookservice.payload.IncomingRequestResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface IncomingRequestService {

    IncomingRequestDTO handleIncomingRequest(String customEndpoint, HttpServletRequest request,
                                              String body, Map<String, String> headers, String email)
            throws JsonProcessingException;

    IncomingRequestResponse getIncomingRequestsByEndpointName(Integer pageNumber, Integer pageSize,
                                                               String sortBy, String sortOrder,
                                                               HttpServletRequest request,
                                                               String endpointName, String email);

    IncomingRequestDTO getSingleRequestForEndpoint(HttpServletRequest request, String endpointName,
                                                    Long requestId, String email);

    void deleteAllRequestsForEndpoint(String endpointName, HttpServletRequest request, String email);

    IncomingRequestDTO deleteRequestForEndpoint(String endpointName, Long requestId,
                                                 HttpServletRequest request, String email);
}
