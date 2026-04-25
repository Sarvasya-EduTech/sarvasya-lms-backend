package com.sarvasya.sarvasya_lms_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.sarvasya.sarvasya_lms_backend.api.ApiErrorResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> bucketsByKey = new ConcurrentHashMap<>();

    private final int authRequestsPerMinute;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(
            @Value("${security.rate-limit.auth.requests-per-minute:20}") int authRequestsPerMinute,
            ObjectMapper objectMapper
    ) {
        this.authRequestsPerMinute = authRequestsPerMinute;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) {
            return true;
        }
        return !(path.startsWith("/sarvasya/auth/") || path.matches("^/[^/]+/auth/.*$"));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String key = clientKey(request);
        Bucket bucket = bucketsByKey.computeIfAbsent(key, k -> Bucket.builder().addLimit(authLimit()).build());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ApiErrorResponse body = ApiErrorResponse.of(
                429,
                "rate_limited",
                "Too many requests",
                request.getRequestURI(),
                Map.of("key", key)
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private Bandwidth authLimit() {
        Refill refill = Refill.intervally(authRequestsPerMinute, Duration.ofMinutes(1));
        return Bandwidth.classic(authRequestsPerMinute, refill);
    }

    private static String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}








