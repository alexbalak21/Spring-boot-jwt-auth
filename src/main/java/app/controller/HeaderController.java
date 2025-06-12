package app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeaderController {

    @GetMapping("/header")
    public String getHeader(@RequestHeader("Authorization") String authorizationHeader) {
        return "Authorization Header: " + authorizationHeader;
    }
}
