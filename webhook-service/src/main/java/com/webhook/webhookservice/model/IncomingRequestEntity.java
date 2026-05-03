package com.webhook.webhookservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "W_INCOMING_REQUEST")
@Getter
@Setter
@NoArgsConstructor
public class IncomingRequestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "r_request_id")
    private long requestId;

    @Column(name = "r_method")
    private String method;

    @Lob
    @Column(name = "r_headers", columnDefinition = "LONGTEXT")
    private String headers;

    @Lob
    @Column(name = "r_body")
    private String body;

    @Lob
    @Column(name = "r_query_params")
    private String queryParams;

    @Lob
    @Column(name = "r_path")
    private String path;

    @Column(name = "r_received_at")
    private LocalDateTime receivedAt;

    @Column(name = "r_ip_address")
    private String ipAddress;

    @ManyToOne
    @JoinColumn(name = "e_endpoint_id")
    private EndpointEntity endpoint;

    // -------- Builder --------
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String method, headers, body, queryParams, path, ipAddress;
        private LocalDateTime receivedAt;
        private EndpointEntity endpoint;

        public Builder method(String v)       { this.method = v; return this; }
        public Builder headers(String v)      { this.headers = v; return this; }
        public Builder body(String v)         { this.body = v; return this; }
        public Builder queryParams(String v)  { this.queryParams = v; return this; }
        public Builder path(String v)         { this.path = v; return this; }
        public Builder ipAddress(String v)    { this.ipAddress = v; return this; }
        public Builder receivedAt(LocalDateTime v) { this.receivedAt = v; return this; }
        public Builder endpoint(EndpointEntity v)  { this.endpoint = v; return this; }

        public IncomingRequestEntity build() {
            IncomingRequestEntity e = new IncomingRequestEntity();
            e.method = method; e.headers = headers; e.body = body;
            e.queryParams = queryParams; e.path = path;
            e.ipAddress = ipAddress; e.receivedAt = receivedAt;
            e.endpoint = endpoint;
            return e;
        }
    }
}
