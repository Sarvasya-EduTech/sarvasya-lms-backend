package com.sarvasya.sarvasya_lms_backend.config;

import java.util.Map;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class HibernateConfig implements BeanPostProcessor {

    private final CurrentTenantIdentifierResolver<?> currentTenantIdentifierResolver;
    private final MultiTenantConnectionProvider<?> multiTenantConnectionProvider;

    public HibernateConfig(CurrentTenantIdentifierResolver<?> currentTenantIdentifierResolver,
                           MultiTenantConnectionProvider<?> multiTenantConnectionProvider) {
        this.currentTenantIdentifierResolver = currentTenantIdentifierResolver;
        this.multiTenantConnectionProvider = multiTenantConnectionProvider;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LocalContainerEntityManagerFactoryBean) {
            LocalContainerEntityManagerFactoryBean emfb = (LocalContainerEntityManagerFactoryBean) bean;
            Map<String, Object> jpaProperties = emfb.getJpaPropertyMap();
            jpaProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
            jpaProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        }
        return bean;
    }
}








