package com.sarvasya.sarvasya_lms_backend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }

        Claims claims = null;
        if (jwt != null) {
            try {
                claims = jwtUtil.extractAllClaims(jwt);
                username = claims.getSubject();
            } catch (Exception e) {
                // Invalid token - treat request as unauthenticated
                username = null;
                claims = null;
            }
        }

        if (username != null && claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String tokenTenant = (String) claims.get("tenantId");
            String originalTenant = TenantContext.getTenantId();

            try {
                if (tokenTenant != null && !tokenTenant.isBlank()) {
                    TenantContext.setTenantId(tokenTenant);
                }

                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(claims, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } finally {
                if (originalTenant != null) {
                    TenantContext.setTenantId(originalTenant);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}








