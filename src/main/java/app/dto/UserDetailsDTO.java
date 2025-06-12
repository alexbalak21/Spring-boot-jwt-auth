package app.dto;

public class UserDetailsDTO {
    private final String email;
    private final String uid;
    private final String role;

    public UserDetailsDTO(String email, String uid, String role) {
        this.email = email;
        this.uid = uid;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getRole() {
        return role;
    }

}
