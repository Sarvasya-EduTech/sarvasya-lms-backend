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

        if (path.startsWith("/api/auth/")) {
            TenantContext.setTenantId("tenant");
        } else if (path.startsWith("/api/v1/")) {
            String[] parts = path.split("/");
            // Example: /api/v1/tenants/harvard/theme -> parts.length = 6, parts[4] =
            // harvard
            // Parts: ["", "api", "v1", "tenants", "harvard", "theme"]
            if (path.startsWith("/api/v1/tenants/") && parts.length > 5) {
                // Has a sub-resource like /api/v1/tenants/{tenantId}/theme or
                // /api/v1/tenants/{tenantId}/impersonate
                TenantContext.setTenantId(parts[4]); // Extract specific tenant
            } else {
                // Global management: /api/v1/tenants, /api/v1/limits, /api/v1/users
                // Or tenant config endpoints: /api/v1/tenants/{tenantId}
                TenantContext.setTenantId("tenant"); // Default to central platform schema
            }
        } else if (path.startsWith("/api/")) {
            String[] parts = path.split("/");
            if (parts.length >= 3) {
                String tenantName = parts[2];
                if (!tenantName.equals("v1") && !tenantName.equals("auth")) {
                    TenantContext.setTenantId(tenantName);
                }
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
