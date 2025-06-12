package app.controller;

import app.dto.UserDetailsDTO;
import app.dto.UserRequestDTO;
import app.enums.AuthResult;
import app.model.User;
import app.repository.UserRepository;
import app.service.AuthenticationService;
import app.utils.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public LoginController(AuthenticationService authenticationService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody UserRequestDTO request) {
        AuthResult result = authenticationService.authenticate(request.getEmail(), request.getPassword());

        if (result == AuthResult.SUCCESS) {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UUID uid = user.getUid();
            UserDetailsDTO userDetailsDTO = new UserDetailsDTO(user.getEmail(), uid.toString(), user.getRole().name());
            String token = Jwt.generateToken(userDetailsDTO);

            return ResponseEntity.ok(Map.of("message", "Login successful", "accessToken", token));
        }

        Map<String, String> response = Map.of("message",
                result == AuthResult.USER_NOT_FOUND ? "User not found" : "Invalid password");

        return ResponseEntity.status(result == AuthResult.USER_NOT_FOUND ? 404 : 401).body(response);
    }

}
