package com.webhook.userservice.payload;

public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private Boolean isEmailVerified;

    public UserResponse() {}
    public UserResponse(Long id, String email, String name, Boolean isEmailVerified) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.isEmailVerified = isEmailVerified;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Boolean getIsEmailVerified() { return isEmailVerified; }
    public void setIsEmailVerified(Boolean isEmailVerified) { this.isEmailVerified = isEmailVerified; }
}
