package app.service;

import app.dto.UserDTO;
import app.model.User;
import app.model.UserRole;
import app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create User
    public void createUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setAccountLocked(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUid(UUID.randomUUID());
        user.setRole(UserRole.VISITOR);
        userRepository.save(user);
    }

    // Read All Users (Returns DTOs with only id and username)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUsers();
    }

    // Read User By Email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Read User By ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Update User
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEmail(updatedUser.getEmail());
                    user.setPassword(passwordEncoder.encode(updatedUser.getPassword())); // Re-encode password
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Delete User
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}