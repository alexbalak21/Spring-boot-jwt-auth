package app.controller;

import app.dto.UserDetailsDTO;
import app.security.LoginRequired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user") // This correctly sets the base path
public class UserController {

    @GetMapping("/home")
    public String user() {
        return "user page"; // Now accessible at GET /user/
    }

    @LoginRequired
    @GetMapping("/me")
    public String getUser() {
        return "me";
        //return (UserDetailsDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

