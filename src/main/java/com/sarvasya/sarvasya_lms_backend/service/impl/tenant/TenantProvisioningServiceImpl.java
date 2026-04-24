package com.sarvasya.sarvasya_lms_backend.service.impl.tenant;

import com.sarvasya.sarvasya_lms_backend.config.FlywayConfig;
import com.sarvasya.sarvasya_lms_backend.config.db.ShardDataSourceManager;
import com.sarvasya.sarvasya_lms_backend.service.tenant.TenantProvisioningService;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantProvisioningServiceImpl implements TenantProvisioningService {

    private final FlywayConfig flywayConfig;
    private final DataSource dataSource;
    private final ObjectProvider<ShardDataSourceManager> shardDataSourceManagerProvider;

    @Override
    public void provisionTenantSchema(String tenantId) {
        String normalized = normalizeTenantId(tenantId);

        DataSource writer = dataSource;
        ShardDataSourceManager shardManager = shardDataSourceManagerProvider.getIfAvailable();
        if (shardManager != null && shardManager.isEnabled()) {
            writer = shardManager.writerForTenant(normalized);
        }

        flywayConfig.migrateTenantSchema(writer, normalized);
    }

    private static String normalizeTenantId(String tenantId) {
        if (tenantId == null) throw new IllegalArgumentException("tenantId is required");
        String normalized = tenantId.trim().toLowerCase();
        if (normalized.isEmpty()) throw new IllegalArgumentException("tenantId is required");
        if (!normalized.matches("[a-z0-9_\\-]+")) throw new IllegalArgumentException("Invalid tenantId");
        if ("public".equals(normalized) || "tenant".equals(normalized)) {
            throw new IllegalArgumentException("Invalid tenantId");
        }
        return normalized;
    }
}

