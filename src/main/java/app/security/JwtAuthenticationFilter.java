package app.security;

import app.dto.UserDetailsDTO;
import app.model.User;
import app.repository.UserRepository;
import app.utils.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("Executing JwtAuthenticationFilter...");

        String authHeader = request.getHeader("Authorization");
        System.out.println("Auth Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Authorization header missing or incorrect.");
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("Extracted Token: " + token);

        if (!Jwt.validateToken(token)) {
            System.out.println("Invalid JWT Token.");
            chain.doFilter(request, response);
            return;
        }

        String email = Jwt.getEmailFromToken(token);
        System.out.println("Extracted Email from Token: " + email);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            System.out.println("User not found in database.");
            chain.doFilter(request, response);
            return;
        }

        System.out.println("User found: " + user.getEmail() + " | Role: " + user.getRole());

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(user.getEmail(), user.getUid().toString(), user.getRole().name());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetailsDTO, null, List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);
        System.out.println("Authentication set in SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication());

        chain.doFilter(request, response);
    }

}
