package app.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/signup")
public class SignInController {

    @PostMapping
    String signup() {
        return "signup";
    }
}
