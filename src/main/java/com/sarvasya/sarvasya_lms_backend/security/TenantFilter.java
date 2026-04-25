package com.sarvasya.sarvasya_lms_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String DEFAULT_TENANT = "tenant";
    private static final String SARVASYA_PREFIX = "/sarvasya/";
    private static final Set<String> GLOBAL_SARVASYA_ROOTS = Set.of(
            "auth",
            "tenants",
            "limits",
            "users",
            "buses",
            "bus-passes",
            "bus-schedules",
            "bus-stops",
            "courses",
            "modules",
            "lessons",
            "studymaterials",
            "quizs",
            "exams",
            "questions",
            "options",
            "quizquestions",
            "examquestions",
            "upload"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        TenantContext.setTenantId(resolveTenantId(request.getRequestURI()));

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String resolveTenantId(String path) {
        if (path == null || path.isBlank() || "/".equals(path)) {
            return DEFAULT_TENANT;
        }

        String[] parts = path.split("/");
        if (parts.length < 2) {
            return DEFAULT_TENANT;
        }

        // Rule 1 + 2:
        // /sarvasya/{tenantId}/... -> specific tenant
        // /sarvasya/... -> global (tenant schema)
        if (path.startsWith(SARVASYA_PREFIX)) {
            if (parts.length >= 4) {
                String segment = parts[2];
                if (!GLOBAL_SARVASYA_ROOTS.contains(segment)) {
                    return segment;
                }
            }
            return DEFAULT_TENANT;
        }

        // Rule 3: /{tenantId}/... -> specific tenant
        if (parts.length >= 3 && parts[1] != null && !parts[1].isBlank()) {
            return parts[1];
        }
        return DEFAULT_TENANT;
    }
}








