package app.security;

import app.dto.UserDetailsDTO;
import app.model.User;
import app.repository.UserRepository;
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

    private final UserRepository userRepository;
    private static final ThreadLocal<UserDetailsDTO> currentUser = new ThreadLocal<>();

    public LoginAspect(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Before("@annotation(app.security.LoginRequired)")
    public void checkAuthentication() {
        String token = getToken();
        if (!Jwt.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token");
        }

        // Extract user details from the JWT token
        UserDetailsDTO userDetails = Jwt.getUserDetailsFromToken(token);

        // Retrieve user from the database and check status
        User user = userRepository.findByEmail(userDetails.getEmail()).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist");
        }

        if (!user.isActive()) { // Assuming you have an `isActive()` method in User
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not active");
        }

        currentUser.set(userDetails); // Store the authenticated user
    }

    private static String getToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Request not available");
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        return authHeader.substring(7);
    }

    public static UserDetailsDTO getAuthenticatedUser() {
        return currentUser.get();
    }
}
