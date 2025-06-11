package app.service;

import app.model.User;
import app.repository.UserRepository;
import app.enums.AuthResult;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthResult authenticate(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return AuthResult.USER_NOT_FOUND;
        }

        User user = userOptional.get();
        return passwordEncoder.matches(password, user.getPassword())
                ? AuthResult.SUCCESS
                : AuthResult.INVALID_PASSWORD;
    }
}
