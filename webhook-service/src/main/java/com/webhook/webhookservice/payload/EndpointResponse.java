package com.webhook.webhookservice.payload;

import java.util.List;

public class EndpointResponse {
    private List<EndpointDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;

    public EndpointResponse() {}

    public List<EndpointDTO> getContent() { return content; }
    public void setContent(List<EndpointDTO> content) { this.content = content; }
    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public Long getTotalElements() { return totalElements; }
    public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }
    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
    public boolean isLastPage() { return lastPage; }
    public void setLastPage(boolean lastPage) { this.lastPage = lastPage; }
}
