package com.webhook.webhookservice.payload;

import java.util.Map;

public class IncomingRequestDTO {
    private Long requestId;
    private String method;
    private Map<String, String> headers;
    private String body;
    private String queryParams;
    private String path;
    private String ipAddress;
    private Long endpointId;
    private String endpointName;

    public IncomingRequestDTO() {}

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getQueryParams() { return queryParams; }
    public void setQueryParams(String queryParams) { this.queryParams = queryParams; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public Long getEndpointId() { return endpointId; }
    public void setEndpointId(Long endpointId) { this.endpointId = endpointId; }
    public String getEndpointName() { return endpointName; }
    public void setEndpointName(String endpointName) { this.endpointName = endpointName; }
}
