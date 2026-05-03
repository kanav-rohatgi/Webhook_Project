package com.webhook.webhookservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webhook.webhookservice.exception.APIException;
import com.webhook.webhookservice.exception.ResourceNotFoundException;
import com.webhook.webhookservice.model.EndpointEntity;
import com.webhook.webhookservice.model.IncomingRequestEntity;
import com.webhook.webhookservice.payload.IncomingRequestDTO;
import com.webhook.webhookservice.payload.IncomingRequestResponse;
import com.webhook.webhookservice.repository.EndpointRepository;
import com.webhook.webhookservice.repository.IncomingRequestRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class IncomingRequestServiceImpl implements IncomingRequestService {

    private final EndpointRepository endpointRepository;
    private final IncomingRequestRepository incomingRequestRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IncomingRequestServiceImpl(EndpointRepository endpointRepository,
                                       IncomingRequestRepository incomingRequestRepository) {
        this.endpointRepository = endpointRepository;
        this.incomingRequestRepository = incomingRequestRepository;
    }

    @Override
    public IncomingRequestDTO handleIncomingRequest(String customEndpoint, HttpServletRequest request,
                                                     String body, Map<String, String> headers, String email)
            throws JsonProcessingException {

        EndpointEntity endpoint = endpointRepository
                .findByEndpointNameAndUserEmail(customEndpoint, email)
                .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", customEndpoint));

        String method = request.getMethod();
        String headersJson = objectMapper.writeValueAsString(headers);
        String queryParams = request.getQueryString();
        String path = request.getRequestURI();

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        IncomingRequestEntity incomingRequest = IncomingRequestEntity.builder()
                .method(method)
                .headers(headersJson)
                .body(body)
                .queryParams(queryParams)
                .path(path)
                .ipAddress(ipAddress)
                .receivedAt(LocalDateTime.now())
                .endpoint(endpoint)
                .build();

        IncomingRequestEntity saved = incomingRequestRepository.save(incomingRequest);

        IncomingRequestDTO dto = toDTO(saved, endpoint);
        dto.setHeaders(objectMapper.readValue(saved.getHeaders(), new TypeReference<>() {}));
        return dto;
    }

    @Override
    public IncomingRequestResponse getIncomingRequestsByEndpointName(Integer pageNumber, Integer pageSize,
                                                                      String sortBy, String sortOrder,
                                                                      HttpServletRequest request,
                                                                      String endpointName, String email) {
        EndpointEntity endpoint = endpointRepository
                .findByEndpointNameAndUserEmail(endpointName, email)
                .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", endpointName));

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<IncomingRequestEntity> page = incomingRequestRepository
                .findByEndpointOrderByReceivedAtAsc(endpoint, pageable);

        List<IncomingRequestEntity> requests = page.getContent();
        if (requests.isEmpty()) {
            throw new APIException("No requests at this endpoint yet");
        }

        List<IncomingRequestDTO> dtos = requests.stream().map(req -> {
            IncomingRequestDTO dto = toDTO(req, req.getEndpoint());
            try {
                dto.setHeaders(objectMapper.readValue(req.getHeaders(), new TypeReference<>() {}));
            } catch (JsonProcessingException e) {
                dto.setHeaders(null);
            }
            return dto;
        }).toList();

        IncomingRequestResponse response = new IncomingRequestResponse();
        response.setContent(dtos);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());
        return response;
    }

    @Override
    public IncomingRequestDTO getSingleRequestForEndpoint(HttpServletRequest request,
                                                           String endpointName, Long requestId, String email) {
        EndpointEntity endpoint = endpointRepository
                .findByEndpointNameAndUserEmail(endpointName, email)
                .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", endpointName));

        IncomingRequestEntity incomingRequest = endpoint.getIncomingRequests().stream()
                .filter(r -> requestId.equals(r.getRequestId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Request", "requestId", requestId));

        IncomingRequestDTO dto = toDTO(incomingRequest, endpoint);
        try {
            dto.setHeaders(objectMapper.readValue(incomingRequest.getHeaders(), new TypeReference<>() {}));
        } catch (JsonProcessingException e) {
            dto.setHeaders(null);
        }
        return dto;
    }

    @Override
    public void deleteAllRequestsForEndpoint(String endpointName, HttpServletRequest request, String email) {
        EndpointEntity endpoint = endpointRepository
                .findByEndpointNameAndUserEmail(endpointName, email)
                .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", endpointName));

        if (endpoint.getIncomingRequests().isEmpty()) {
            throw new APIException("No requests found at this endpoint");
        }
        endpoint.getIncomingRequests().clear();
        endpointRepository.save(endpoint);
    }

    @Override
    public IncomingRequestDTO deleteRequestForEndpoint(String endpointName, Long requestId,
                                                        HttpServletRequest request, String email) {
        EndpointEntity endpoint = endpointRepository
                .findByEndpointNameAndUserEmail(endpointName, email)
                .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", endpointName));

        IncomingRequestEntity incomingRequest = endpoint.getIncomingRequests().stream()
                .filter(r -> requestId.equals(r.getRequestId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Request", "requestId", requestId));

        endpoint.getIncomingRequests().remove(incomingRequest);
        endpointRepository.save(endpoint);

        IncomingRequestDTO dto = toDTO(incomingRequest, endpoint);
        try {
            dto.setHeaders(objectMapper.readValue(incomingRequest.getHeaders(), new TypeReference<>() {}));
        } catch (JsonProcessingException e) {
            dto.setHeaders(null);
        }
        return dto;
    }

    // -------- helper --------
    private IncomingRequestDTO toDTO(IncomingRequestEntity entity, EndpointEntity endpoint) {
        IncomingRequestDTO dto = new IncomingRequestDTO();
        dto.setRequestId(entity.getRequestId());
        dto.setMethod(entity.getMethod());
        dto.setBody(entity.getBody());
        dto.setQueryParams(entity.getQueryParams());
        dto.setPath(entity.getPath());
        dto.setIpAddress(entity.getIpAddress());
        dto.setEndpointId(endpoint.getEndpointId());
        dto.setEndpointName(endpoint.getEndpointName());
        return dto;
    }
}
