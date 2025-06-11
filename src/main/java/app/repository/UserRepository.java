package app.repository;

import app.dto.UserDTO;
import app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Returns a single user wrapped in Optional

    @Query("SELECT new app.dto.UserDTO(u.id, u.username) FROM User u")
    List<UserDTO> findAllUsers(); // Custom query to return DTOs
}