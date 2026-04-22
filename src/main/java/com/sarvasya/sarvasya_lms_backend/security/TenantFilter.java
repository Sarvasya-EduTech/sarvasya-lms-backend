package com.sarvasya.sarvasya_lms_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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
            if (parts.length == 3) {
                // Global request: /api/calendar, /api/users
                TenantContext.setTenantId("tenant");
            } else if (parts.length >= 4) {
                String segment2 = parts[2]; // can be 'v1', 'auth', 'sarvasya', or {tenantId}
                if (segment2.equals("sarvasya") && parts.length >= 5) {
                    // Check if it's /api/sarvasya/{tenantId}/...
                    String possibleTenant = parts[3];
                    // List of global sarvasya sub-paths (if any, currently we assume /api/sarvasya/{resource} is global)
                    // If parts[3] is a resource name (courses, modules, etc.), it's global.
                    // If it's a tenant name, it's tenant-scoped.
                    List<String> globalResources = List.of("courses", "modules", "lessons", "studymaterials", "quizs", "exams", "questions", "options", "quizquestions", "examquestions", "upload");
                    if (globalResources.contains(possibleTenant)) {
                        TenantContext.setTenantId("tenant");
                    } else {
                        TenantContext.setTenantId(possibleTenant);
                    }
                } else if (!segment2.equals("v1") && !segment2.equals("auth") && !segment2.equals("sarvasya")) {
                    TenantContext.setTenantId(segment2);
                } else {
                    TenantContext.setTenantId("tenant");
                }
            } else {
                TenantContext.setTenantId("tenant");
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
