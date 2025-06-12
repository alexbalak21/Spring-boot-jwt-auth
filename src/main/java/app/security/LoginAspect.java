package app.security;

import app.dto.UserDetailsDTO;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.JoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class LoginAspect {

    @Before("@annotation(app.security.LoginRequired)")
    public void checkAuthentication(JoinPoint joinPoint) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetailsDTO)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "{\"message\":\"Unauthorized\", \"warning\": Login required}");

        }
    }
}
