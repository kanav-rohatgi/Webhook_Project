package com.webhook.webhookservice.service;

import com.webhook.webhookservice.exception.APIException;
import com.webhook.webhookservice.exception.ResourceNotFoundException;
import com.webhook.webhookservice.model.EndpointEntity;
import com.webhook.webhookservice.payload.EndpointDTO;
import com.webhook.webhookservice.payload.EndpointResponse;
import com.webhook.webhookservice.repository.EndpointRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EndpointServiceImpl implements EndpointService {

    private final EndpointRepository endpointRepository;

    public EndpointServiceImpl(EndpointRepository endpointRepository) {
        this.endpointRepository = endpointRepository;
    }

    @Value("${gateway.base-url}")
    private String gatewayBaseUrl;


    @Override
    public EndpointDTO createEndpoint(EndpointDTO endpointDTO, HttpServletRequest request, String email) {
        // Check duplicate name
        if (endpointRepository.findByEndpointName(endpointDTO.getEndpointName()).isPresent()) {
            throw new APIException("Endpoint with name " + endpointDTO.getEndpointName() + " already exists");
        }
        EndpointEntity endpoint = new EndpointEntity();
        endpoint.setEndpointName(endpointDTO.getEndpointName());
        endpoint.setDescription(endpointDTO.getDescription());
        endpoint.setUserEmail(email);

        EndpointEntity saved = endpointRepository.save(endpoint);
        EndpointDTO result = toDTO(saved);
        result.setCustomEndpointUrl(gatewayBaseUrl + "/api/" + saved.getEndpointName());
        return result;
    }

    @Override
    public EndpointResponse getAllEndpoints(Integer pageNumber, Integer pageSize, String sortBy,
                                            String sortOrder, HttpServletRequest request, String email) {
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<EndpointEntity> page = endpointRepository.findByUserEmail(email, pageable);
        List<EndpointEntity> endpoints = page.getContent();
        if (endpoints.isEmpty()) {
            throw new APIException("No endpoints created yet");
        }

        String baseUrl = buildBaseUrl(request);
        List<EndpointDTO> dtos = endpoints.stream().map(ep -> {
            EndpointDTO dto = toDTO(ep);
            dto.setCustomEndpointUrl(baseUrl + "/api/" + ep.getEndpointName());
            return dto;
        }).toList();

        EndpointResponse response = new EndpointResponse();
        response.setContent(dtos);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());
        return response;
    }

    @Override
    public EndpointDTO searchEndpointById(Long endpointId, HttpServletRequest request, String email) {
        EndpointEntity endpoint = endpointRepository.findById(endpointId)
                .filter(e -> e.getUserEmail().equals(email))
                .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointId", endpointId));

        EndpointDTO dto = toDTO(endpoint);
        dto.setCustomEndpointUrl(buildBaseUrl(request) + "/api/" + endpoint.getEndpointName());
        return dto;
    }

    @Override
    public EndpointDTO searchEndpointByName(String endpointName, HttpServletRequest request, String email) {
        EndpointEntity endpoint = endpointRepository
                .findByEndpointNameAndUserEmail(endpointName, email)
                .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", endpointName));

        EndpointDTO dto = toDTO(endpoint);
        dto.setCustomEndpointUrl(buildBaseUrl(request) + "/api/" + endpoint.getEndpointName());
        return dto;
    }

    @Override
    public EndpointDTO deleteEndpoint(Long endpointId, HttpServletRequest request, String email) {
        EndpointEntity endpoint = endpointRepository.findById(endpointId)
                .filter(e -> e.getUserEmail().equals(email))
                .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointId", endpointId));

        endpointRepository.delete(endpoint);

        EndpointDTO dto = toDTO(endpoint);
        dto.setCustomEndpointUrl(buildBaseUrl(request) + "/api/" + endpoint.getEndpointName());
        return dto;
    }

    // -------- helpers --------
    private EndpointDTO toDTO(EndpointEntity e) {
        EndpointDTO dto = new EndpointDTO();
        dto.setEndpointId(e.getEndpointId());
        dto.setEndpointName(e.getEndpointName());
        dto.setDescription(e.getDescription());
        return dto;
    }

    private String buildBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
