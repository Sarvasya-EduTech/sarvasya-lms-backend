package com.sarvasya.sarvasya_lms_backend.config.db;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.db.sharding")
public class ShardingProperties {

    private boolean enabled = false;

    /**
     * Map key is the shard id (e.g. shard0, shard1).
     */
    private Map<String, Shard> shards = new LinkedHashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, Shard> getShards() {
        return shards;
    }

    public void setShards(Map<String, Shard> shards) {
        this.shards = shards;
    }

    public static class Shard {
        private Node writer;
        private List<Node> readers;

        public Node getWriter() {
            return writer;
        }

        public void setWriter(Node writer) {
            this.writer = writer;
        }

        public List<Node> getReaders() {
            return readers;
        }

        public void setReaders(List<Node> readers) {
            this.readers = readers;
        }
    }

    public static class Node {
        private String url;
        private String username;
        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

