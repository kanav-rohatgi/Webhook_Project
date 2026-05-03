package com.webhook.webhookservice.service;

import com.webhook.webhookservice.payload.EndpointDTO;
import com.webhook.webhookservice.payload.EndpointResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface EndpointService {

    EndpointDTO createEndpoint(EndpointDTO endpointDTO, HttpServletRequest request, String email);

    EndpointResponse getAllEndpoints(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,
                                     HttpServletRequest request, String email);

    EndpointDTO searchEndpointById(Long endpointId, HttpServletRequest request, String email);

    EndpointDTO searchEndpointByName(String endpointName, HttpServletRequest request, String email);

    EndpointDTO deleteEndpoint(Long endpointId, HttpServletRequest request, String email);
}
