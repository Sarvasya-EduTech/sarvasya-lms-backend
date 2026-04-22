package com.sarvasya.sarvasya_lms_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;

import java.io.IOException;

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
        } else {
            jwt = request.getParameter("token");
        }

        Claims claims = null;
        if (jwt != null) {
            try {
                claims = jwtUtil.extractAllClaims(jwt);
                username = claims.getSubject();
            } catch (Exception e) {
                // Token parsing failed, will proceed to unauthorized
            }
        }

        if (username != null && claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String tokenTenant = (String) claims.get("tenantId");
            String originalTenant = TenantContext.getTenantId();
            if (tokenTenant != null) {
                // For authentication purposes, we MUST be in the user's tenant context
                TenantContext.setTenantId(tokenTenant);
            }

            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

            // Restore original tenant context for the rest of the request (e.g. for schema-based data access)
            if (originalTenant != null && !originalTenant.equals(TenantContext.getTenantId())) {
                String path = request.getRequestURI();
                if (originalTenant.equals("tenant") && path.startsWith("/api/sarvasya/")) {
                    TenantContext.setTenantId("tenant");
                } else if (!originalTenant.equals("tenant")) {
                    TenantContext.setTenantId(originalTenant);
                }
            }

            if (jwtUtil.validateToken(claims, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
