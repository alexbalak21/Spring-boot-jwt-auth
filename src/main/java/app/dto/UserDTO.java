package app.dto;

import java.util.Date;

public record UserDTO(Long id, String email, boolean accountLocked, Date createdAt, Date updatedAt, String role) {
}
