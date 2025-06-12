package app.controller;

import app.dto.UserDetailsDTO;
import app.security.LoginAspect;
import app.security.LoginRequired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @LoginRequired
    @GetMapping("/me")
    public UserDetailsDTO getUser() {
        UserDetailsDTO userDetails = LoginAspect.getAuthenticatedUser();

        if (userDetails == null) {
            throw new RuntimeException("User details not found");
        }

        return userDetails;
    }
}
