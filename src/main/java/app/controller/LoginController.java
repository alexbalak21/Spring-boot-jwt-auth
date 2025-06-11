package app.controller;

import app.dto.UserRequestDTO;
import app.enums.AuthResult;
import app.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<String> login(@RequestBody UserRequestDTO request) {
        AuthResult result = authenticationService.authenticate(request.getEmail(), request.getPassword());

        return switch (result) {
            case SUCCESS -> ResponseEntity.ok("{\"message\": \"Login successful\"}");
            case USER_NOT_FOUND -> ResponseEntity.status(404).body("{\"message\": \"User not found\"}");
            case INVALID_PASSWORD -> ResponseEntity.status(401).body("{\"message\": \"Invalid password\"}");
            default -> ResponseEntity.status(500).body("{\"message\": \"Unknown error\"}");
        };
    }
}
