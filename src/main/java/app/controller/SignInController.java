package app.controller;

import app.dto.UserRequestDTO;
import app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/signup")
public class SignInController {

    private final UserService userService;

    public SignInController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> signup(@RequestBody UserRequestDTO request) {
        userService.createUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok("{\"message\": \"User created successfully\"}");
    }
}
