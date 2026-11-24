package com.foodrescue.security.jwt;

import com.foodrescue.security.context.AuthUser;
import com.foodrescue.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Validates the Bearer token, then re-loads the user so a disabled account or changed role takes effect
 * immediately (the role in the token is informational for clients; the database is authoritative).
 * Not a @Component on purpose: it is created inside SecurityConfig so it is not registered twice.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository users;

    public JwtAuthFilter(JwtService jwtService, UserRepository users) {
        this.jwtService = jwtService;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.parse(header.substring(7));
                Long userId = Long.valueOf(claims.getSubject());
                int tokenVersion = JwtService.versionOf(claims);
                // the database is authoritative: disabled users and logged-out tokens are rejected immediately
                users.findById(userId)
                        .filter(u -> u.isEnabled() && u.getTokenVersion() == tokenVersion)
                        .ifPresent(u -> {
                            AuthUser principal = new AuthUser(u.getId(), u.getEmail(), u.getRole());
                            var auth = new UsernamePasswordAuthenticationToken(principal, null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name())));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        });
            } catch (JwtException | IllegalArgumentException ex) {
                // invalid token: leave the context empty -> 401 from the entry point
            }
        }
        chain.doFilter(request, response);
    }
}
