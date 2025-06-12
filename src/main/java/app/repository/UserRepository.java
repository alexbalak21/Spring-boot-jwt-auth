package app.repository;

import app.dto.UserDTO;
import app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Returns a single user wrapped in Optional

    @Query("SELECT new app.dto.UserDTO(u.id, u.email, u.accountLocked, u.createdAt, u.updatedAt, CAST(u.role AS string)) FROM User u")
    List<UserDTO> findAllUsers();
}