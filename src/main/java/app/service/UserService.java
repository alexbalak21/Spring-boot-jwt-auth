package app.service;

import app.dto.UserDTO;
import app.model.User;
import app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create User
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password before saving
        return userRepository.save(user);
    }

    // Read All Users (Returns DTOs with only id and username)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUsers();
    }

    // Read User By Username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
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