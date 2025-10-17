package com.savebot.security;

import com.savebot.model.User;
import com.savebot.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;
    private final UserRepository repo;

    public JwtAuthFilter(JwtUtil jwt, UserRepository repo) {
        this.jwt = jwt;
        this.repo = repo;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/") || "/ping".equals(path) || "/error".equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();

            if (!jwt.validate(token)) {
                // Make invalid token explicit
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                return;
            }

            String email = jwt.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User u = repo.findByEmail(email).orElse(null);
                if (u != null) {
                    List<SimpleGrantedAuthority> authorities = u.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                            .toList();

                    var authToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        chain.doFilter(request, response);
    }
}
