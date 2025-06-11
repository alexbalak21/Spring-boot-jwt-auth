package app.security;

import app.model.User;
import app.repository.UserRepository;
import app.utils.Jwt;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;



public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return; // Skip filter if no token is provided
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        if (!Jwt.validateToken(token)) {
            chain.doFilter(request, response);
            return; // Skip invalid token
        }

        // Extract email from token and fetch user
        String email = Jwt.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            UserDetails userDetails = new User(user.getEmail(), user.getPassword(), user.getRoles());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth); // Set user in the security context
        }

        chain.doFilter(request, response); // Continue filter chain
    }
}
