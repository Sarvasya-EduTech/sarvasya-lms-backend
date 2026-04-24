package com.sarvasya.sarvasya_lms_backend.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Creates and manages per-shard writer pools and reader pools.
 *
 * Tenant-to-shard selection is stable (hash modulo shard count).
 * Read/write selection is done by the caller (typically transaction readOnly).
 */
@Component
@ConditionalOnProperty(name = "app.db.sharding.enabled", havingValue = "true")
public class ShardDataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ShardDataSourceManager.class);

    private final ShardingProperties props;

    private final List<String> shardIds;
    private final Map<String, DataSource> writerByShardId = new ConcurrentHashMap<>();
    private final Map<String, List<DataSource>> readersByShardId = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> rrByShardId = new ConcurrentHashMap<>();

    public ShardDataSourceManager(ShardingProperties props) {
        this.props = props;

        // Bean is only created when enabled=true, but keep defensive checks
        // to avoid failing hard on partial config.
        if (!props.isEnabled()) {
            this.shardIds = List.of();
            return;
        }
        if (props.getShards() == null || props.getShards().isEmpty()) {
            log.warn("DB sharding enabled but no shards configured; falling back to non-sharded datasource");
            this.shardIds = List.of();
            return;
        }

        List<String> ids = new ArrayList<>(props.getShards().keySet());
        Collections.sort(ids);
        this.shardIds = Collections.unmodifiableList(ids);

        for (String shardId : shardIds) {
            ShardingProperties.Shard shard = props.getShards().get(shardId);
            if (shard == null || shard.getWriter() == null) {
                throw new IllegalStateException("Shard [" + shardId + "] must define writer connection details");
            }
            writerByShardId.put(shardId, createPool(shardId, "writer", shard.getWriter()));

            List<DataSource> readers = new ArrayList<>();
            if (shard.getReaders() != null) {
                for (int i = 0; i < shard.getReaders().size(); i++) {
                    ShardingProperties.Node node = shard.getReaders().get(i);
                    if (node == null || node.getUrl() == null || node.getUrl().isBlank()) {
                        continue;
                    }
                    readers.add(createPool(shardId, "reader-" + i, node));
                }
            }
            readersByShardId.put(shardId, Collections.unmodifiableList(readers));
            rrByShardId.put(shardId, new AtomicInteger(0));
        }
    }

    public boolean isEnabled() {
        return props.isEnabled() && !shardIds.isEmpty();
    }

    public List<String> getShardIds() {
        return shardIds;
    }

    public DataSource writerForShardId(String shardId) {
        return writerByShardId.get(shardId);
    }

    public DataSource writerForTenant(String tenantIdentifier) {
        return writerByShardId.get(shardIdForTenant(tenantIdentifier));
    }

    public DataSource readerForTenant(String tenantIdentifier) {
        String shardId = shardIdForTenant(tenantIdentifier);
        List<DataSource> readers = readersByShardId.getOrDefault(shardId, List.of());
        if (readers.isEmpty()) {
            return writerByShardId.get(shardId);
        }
        int idx = Math.floorMod(rrByShardId.get(shardId).getAndIncrement(), readers.size());
        return readers.get(idx);
    }

    public String shardIdForTenant(String tenantIdentifier) {
        if (!isEnabled()) {
            return null;
        }
        String t = (tenantIdentifier == null || tenantIdentifier.isBlank()) ? "public" : tenantIdentifier;
        int shardCount = shardIds.size();
        int idx = Math.floorMod(Objects.hashCode(t), shardCount);
        return shardIds.get(idx);
    }

    private static HikariDataSource createPool(String shardId, String role, ShardingProperties.Node node) {
        String url = require(node.getUrl(), "url", shardId, role);
        String username = require(node.getUsername(), "username", shardId, role);
        String password = require(node.getPassword(), "password", shardId, role);

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(username);
        cfg.setPassword(password);
        // Must match Hibernate expectations (and main datasource settings).
        cfg.setAutoCommit(false);
        cfg.setPoolName("sarvasya-" + shardId + "-" + role);
        return new HikariDataSource(cfg);
    }

    private static String require(String v, String field, String shardId, String role) {
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing " + field + " for shard [" + shardId + "] " + role);
        }
        return v;
    }
}

