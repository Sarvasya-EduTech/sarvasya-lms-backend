package com.sarvasya.sarvasya_lms_backend.config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

@Configuration
public class VirtualThreadsConfig {

    @Bean(name = TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public AsyncTaskExecutor applicationTaskExecutor() {
        Executor vt = tryCreateVirtualThreadExecutor();
        if (vt != null) {
            return new TaskExecutorAdapter(vt);
        }
        return new SimpleAsyncTaskExecutor("sarvasya-async-");
    }

    private static Executor tryCreateVirtualThreadExecutor() {
        try {
            // Java 21+: Executors.newVirtualThreadPerTaskExecutor()
            Method m = Executors.class.getMethod("newVirtualThreadPerTaskExecutor");
            return (Executor) m.invoke(null);
        } catch (Throwable ignored) {
            return null;
        }
    }
}

