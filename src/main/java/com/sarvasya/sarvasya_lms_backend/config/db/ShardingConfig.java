package com.sarvasya.sarvasya_lms_backend.config.db;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingConfig {}

