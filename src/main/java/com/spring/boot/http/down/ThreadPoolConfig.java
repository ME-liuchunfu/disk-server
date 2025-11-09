package com.spring.boot.http.down;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.*;

/**
 * 线程池配置类
 */
@Getter
@Setter
@ToString
public class ThreadPoolConfig {

    private int corePoolSize = 5;
    private int maximumPoolSize = 10;
    private long keepAliveTime = 60;
    private TimeUnit unit = TimeUnit.SECONDS;
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100);
    private ThreadFactory threadFactory = Thread.ofPlatform().name("download-", 0).factory();
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
    private boolean useVirtualThreads = false;

    // 链式配置方法
    public ThreadPoolConfig corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ThreadPoolConfig maximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public ThreadPoolConfig keepAliveTime(long keepAliveTime, TimeUnit unit) {
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;
        return this;
    }

    public ThreadPoolConfig workQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    public ThreadPoolConfig threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ThreadPoolConfig rejectedExecutionHandler(RejectedExecutionHandler handler) {
        this.handler = handler;
        return this;
    }

    public ThreadPoolConfig useVirtualThreads(boolean useVirtualThreads) {
        this.useVirtualThreads = useVirtualThreads;
        return this;
    }

}
