package app.controller;

import app.dto.UserRequestDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/login")
public class LoginController {

    @PostMapping
    public String login(@RequestBody UserRequestDTO request) {

        return "Login";
    }
}
