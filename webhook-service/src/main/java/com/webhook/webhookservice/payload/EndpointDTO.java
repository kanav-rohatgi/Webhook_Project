package com.webhook.webhookservice.payload;

public class EndpointDTO {

    private Long endpointId;
    private String endpointName;
    private String description;
    private String customEndpointUrl;

    public EndpointDTO() {}

    public Long getEndpointId() { return endpointId; }
    public void setEndpointId(Long endpointId) { this.endpointId = endpointId; }
    public String getEndpointName() { return endpointName; }
    public void setEndpointName(String endpointName) { this.endpointName = endpointName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCustomEndpointUrl() { return customEndpointUrl; }
    public void setCustomEndpointUrl(String customEndpointUrl) { this.customEndpointUrl = customEndpointUrl; }
}
