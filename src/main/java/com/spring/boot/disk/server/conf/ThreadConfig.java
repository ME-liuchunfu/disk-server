package com.spring.boot.disk.server.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.*;

@EnableAsync
@Configuration
public class ThreadConfig {

    public static final String THREAD_POOL_NAME = "virtualThreadPool";

    /**
     * 配置虚拟线程池
     */
    @Bean(name = THREAD_POOL_NAME)
    public Executor virtualThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor() {
            @Override
            protected ExecutorService initializeExecutor(
                    ThreadFactory threadFactory,
                    RejectedExecutionHandler rejectedExecutionHandler) {

                // 使用虚拟线程工厂
                ThreadFactory virtualThreadFactory = Thread.ofVirtual()
                        .name("virtual-thread-", 0)
                        .factory();

                // 创建一个使用虚拟线程的ExecutorService
                return Executors.newThreadPerTaskExecutor(virtualThreadFactory);
            }
        };

        // 核心线程数（对于虚拟线程可以设置较小值）
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() + 1);
        // 最大线程数（虚拟线程可以设置较大值）
        executor.setMaxPoolSize(1000);
        // 队列容量
        executor.setQueueCapacity(10000);
        // 线程名称前缀
        executor.setThreadNamePrefix("virtual-");
        // 初始化
        executor.initialize();

        return executor;
    }

}
