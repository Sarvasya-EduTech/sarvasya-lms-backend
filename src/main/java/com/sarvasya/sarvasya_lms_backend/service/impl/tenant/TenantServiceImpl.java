package com.sarvasya.sarvasya_lms_backend.service.impl.tenant;

import com.sarvasya.sarvasya_lms_backend.model.user.User;
import com.sarvasya.sarvasya_lms_backend.config.db.ShardDataSourceManager;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.api.NotFoundException;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.TenantConfigUpdateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.TenantImpersonationResponse;
import com.sarvasya.sarvasya_lms_backend.model.tenant.TenantConfig;
import com.sarvasya.sarvasya_lms_backend.repository.tenant.TenantConfigRepository;
import com.sarvasya.sarvasya_lms_backend.repository.user.GlobalUserRepository;
import com.sarvasya.sarvasya_lms_backend.security.JwtUtil;
import com.sarvasya.sarvasya_lms_backend.service.tenant.TenantService;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantConfigRepository tenantConfigRepository;
    private final GlobalUserRepository globalUserRepository;
    private final JwtUtil jwtUtil;
    private final ObjectProvider<ShardDataSourceManager> shardDataSourceManagerProvider;
    private final ObjectMapper objectMapper;

    @Override
    public List<TenantConfig> listTenants() {
        ShardDataSourceManager shardManager = shardDataSourceManagerProvider.getIfAvailable();
        if (shardManager == null || !shardManager.isEnabled()) {
            return tenantConfigRepository.findAll();
        }

        // Fan-out query: global "tenant" schema exists on every shard (separate DB),
        // so to list all tenants we must query each shard and merge.
        Map<String, TenantConfig> byTenantId = new LinkedHashMap<>();
        for (String shardId : shardManager.getShardIds()) {
            var ds = shardManager.writerForShardId(shardId);
            if (ds == null) continue;
            JdbcTemplate jdbc = new JdbcTemplate(ds);
            List<TenantConfig> shardTenants = jdbc.query(
                    "select tenant_id, features, limits, license, created_at from tenant.tenant_config",
                    (rs, rowNum) -> TenantConfig.builder()
                            .tenantId(rs.getString("tenant_id"))
                            .features(readJsonMap(rs.getObject("features")))
                            .limits(readJsonMap(rs.getObject("limits")))
                            .license(readJsonMap(rs.getObject("license")))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .build()
            );
            for (TenantConfig t : shardTenants) {
                if (t.getTenantId() != null) {
                    byTenantId.putIfAbsent(t.getTenantId(), t);
                }
            }
        }
        return new ArrayList<>(byTenantId.values());
    }

    private Map<String, Object> readJsonMap(Object value) {
        if (value == null) return null;
        if (value instanceof Map<?, ?> m) {
            @SuppressWarnings("unchecked")
            Map<String, Object> cast = (Map<String, Object>) m;
            return cast;
        }
        try {
            String json = value.toString();
            if (json.isBlank() || "null".equalsIgnoreCase(json)) return null;
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse jsonb value from DB", e);
        }
    }

    @Override
    public TenantConfig getTenant(String tenantId) {
        return tenantConfigRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
    }

    @Override
    @Transactional
    public TenantConfig updateTenantConfig(TenantConfigUpdateRequest request) {
        TenantConfig existing = tenantConfigRepository.findById(request.getTenantId())
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        existing.setFeatures(request.getFeatures());
        existing.setLimits(request.getLimits());
        existing.setLicense(request.getLicense());
        return tenantConfigRepository.save(existing);
    }

    @Override
    public TenantImpersonationResponse impersonate(String tenantId, String currentUserEmail) {
        var currentUser = globalUserRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!tenantConfigRepository.existsById(tenantId)) {
            throw new NotFoundException("Tenant not found");
        }

        String token = jwtUtil.generateImpersonationToken(
                currentUserEmail,
                tenantId,
                currentUser.getRole().getValue());

        return new TenantImpersonationResponse(tenantId, token);
    }
}









