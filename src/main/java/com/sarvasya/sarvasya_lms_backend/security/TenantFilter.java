package com.sarvasya.sarvasya_lms_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        
        // Example path: /api/harvard/auth/login or /api/v1/tenants/harvard/theme
        if (path.startsWith("/api/v1/tenants/")) {
            String[] parts = path.split("/");
            // parts[0] = "", parts[1] = "api", parts[2] = "v1", parts[3] = "tenants", parts[4] = "{tenantName}"
            if (parts.length >= 5) {
                String tenantName = parts[4];
                TenantContext.setTenantId(tenantName);
            }
        } else if (path.startsWith("/api/")) {
            String[] parts = path.split("/");
            // parts[0] = "", parts[1] = "api", parts[2] = "{tenantName}"
            if (parts.length >= 3) {
                String tenantName = parts[2];
                TenantContext.setTenantId(tenantName);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
