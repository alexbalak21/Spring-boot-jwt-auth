package app.security;

import app.utils.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoginAspect {

    @Before("@annotation(app.security.LoginRequired)")
    public void checkAuthentication() {
        System.out.println("Executing LoginRequired Aspect...");

        // Retrieve the current HTTP request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            System.out.println("No current request found.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Request not available");
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header Received: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        // Extract and decode the JWT token
        String token = authHeader.substring(7);
        if (!Jwt.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token");
        }

        String email = Jwt.getEmailFromToken(token);
        System.out.println("Decoded Email from JWT: " + email);
    }
}
