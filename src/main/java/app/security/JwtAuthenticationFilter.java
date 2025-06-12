package app.security;

import app.dto.UserDetailsDTO;
import app.model.User;
import app.repository.UserRepository;
import app.utils.Jwt;
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

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println(token);
        if (!Jwt.validateToken(token)) {
            chain.doFilter(request, response);
            return;
        }

        // Extract user details from token
        String email = Jwt.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            UserDetailsDTO userDetailsDTO = new UserDetailsDTO(user.getEmail(), user.getUid().toString(), user.getRole().name());

            // Properly set role authorities for Spring Security
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetailsDTO, null, List.of(new SimpleGrantedAuthority(user.getRole().name()))
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}
