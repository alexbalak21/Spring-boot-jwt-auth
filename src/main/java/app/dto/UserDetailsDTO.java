package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Allows extra properties like "exp" without error
public class UserDetailsDTO {
    private String email;
    private String uid;
    private String role;

    // No-arg constructor REQUIRED for Jackson
    public UserDetailsDTO() {}

    public UserDetailsDTO(String email, String uid, String role) {
        this.email = email;
        this.uid = uid;
        this.role = role;
    }

    public String getEmail() { return email; }
    public String getUid() { return uid; }
    public String getRole() { return role; }

    public void setEmail(String email) { this.email = email; }
    public void setUid(String uid) { this.uid = uid; }
    public void setRole(String role) { this.role = role; }
}
