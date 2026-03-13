package com.yasirkhan.user.configurations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    @Value("${app.security.internal-secret}")
    private String GATEWAY_SECRET = "";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Corrected: Use startsWith to match all paths beginning with /api/auth/
        return
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Validate the Gateway Secret first
        String incomingSecret = request.getHeader("X-Gateway-Secret");

        if (incomingSecret == null || !incomingSecret.equals(GATEWAY_SECRET)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Direct access blocked: Request must come through API Gateway");
            return; // STOP: Do not call filterChain.doFilter
        }

        // 2. If secret is valid, proceed with your existing Header logic
        String username = request.getHeader("X-Username");
        String role = request.getHeader("X-User-Role");

        System.out.println("User: " + username + " | Role: " + role);
        if (username != null && role != null) {
            System.out.println("User: " + username + " | Role: " + role);
            List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
            System.out.println("Context Auth: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        }

        filterChain.doFilter(request, response);
    }
}