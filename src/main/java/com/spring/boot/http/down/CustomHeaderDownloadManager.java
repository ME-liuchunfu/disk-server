package com.spring.boot.http.down;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

/**
 * JDK 21 多线程下载管理器（支持自定义请求头）
 * 增加了自定义HTTP请求头功能，支持设置User-Agent、Cookie等
 */
public class CustomHeaderDownloadManager implements AutoCloseable {

    // 线程池配置参数
    private final ThreadPoolConfig threadPoolConfig;
    // 线程池实例
    private final ExecutorService executor;
    private boolean closeExecutor = true;
    // 下载任务列表
    private final List<DownloadTask> tasks = new ArrayList<>();
    // HTTP客户端（JDK 11+新API）
    private final HttpClient httpClient;
    // 默认请求头（所有任务共享）
    private final Map<String, List<String>> defaultHeaders = new HashMap<>();

    /**
     * 构造函数，使用默认线程池配置
     */
    public CustomHeaderDownloadManager() {
        this(new ThreadPoolConfig());
    }

    /**
     * 构造函数，使用自定义线程池配置
     */
    public CustomHeaderDownloadManager(ThreadPoolConfig config) {
        this.threadPoolConfig = config;
        this.executor = createExecutor(config);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .executor(executor)
                .build();

        // 设置默认请求头（可被任务级请求头覆盖）
        setDefaultHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1");
        setDefaultHeader("Accept", "*/*");
    }

    public CustomHeaderDownloadManager(ThreadPoolConfig config, ExecutorService executor) {
        this.threadPoolConfig = config;
        this.closeExecutor = Boolean.FALSE;
        this.executor = executor;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .executor(executor)
                .build();

        // 设置默认请求头（可被任务级请求头覆盖）
        setDefaultHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1");
        setDefaultHeader("Accept", "*/*");
    }

    /**
     * 创建线程池（支持虚拟线程）
     */
    private ExecutorService createExecutor(ThreadPoolConfig config) {
        if (config.isUseVirtualThreads()) {
            return Executors.newThreadPerTaskExecutor(
                    Thread.ofVirtual().name("download-virtual-", 0).factory()
            );
        } else {
            return new ThreadPoolExecutor(
                    config.getCorePoolSize(),
                    config.getMaximumPoolSize(),
                    config.getKeepAliveTime(),
                    config.getUnit(),
                    config.getWorkQueue(),
                    config.getThreadFactory(),
                    config.getHandler()
            );
        }
    }

    /**
     * 设置全局默认请求头（所有任务共享）
     */
    public void setDefaultHeader(String name, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        defaultHeaders.put(name, values);
    }

    /**
     * 添加全局默认请求头（不覆盖现有值）
     */
    public void addDefaultHeader(String name, String value) {
        defaultHeaders.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    /**
     * 添加下载任务（使用配置类）
     */
    public void addTask(DownloadTaskConfig config) throws IOException, InterruptedException {
        // 确保父目录存在
        Files.createDirectories(config.getSavePath().getParent());
        tasks.add(new DownloadTask(config));
    }

    /**
     * 开始所有下载任务
     */
    public void startAll() throws InterruptedException, ExecutionException {
        List<Future<?>> futures = new ArrayList<>();
        for (DownloadTask task : tasks) {
            futures.add(executor.submit(task));
        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            future.get();
        }
    }

    /**
     * 关闭资源
     */
    @Override
    public void close() {
        if (closeExecutor && executor != null && !executor.isTerminated()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }

    /**
     * 单个下载任务
     */
    public class DownloadTask implements Runnable {
        private final String url;
        private final Path savePath;
        private final Path tempPath;
        private final Map<String, List<String>> taskHeaders;

        public DownloadTask(DownloadTaskConfig config) {
            this.url = config.getUrl();
            this.savePath = config.getSavePath();
            this.taskHeaders = new HashMap<>(config.getHeaders());
            this.tempPath = savePath.resolveSibling(savePath.getFileName() + DownConstant.TEMP_SUFFIX);
        }

        @Override
        public void run() {
            try {
                System.out.printf("开始下载: %s %n", url);
                // 创建临时文件并设置大小
                Files.write(tempPath, new byte[0], StandardOpenOption.CREATE);
                downloadPart();
                // 下载完成，重命名临时文件
                Files.move(tempPath, savePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.printf("%n下载完成: %s%n", savePath);
            } catch (Exception e) {
                System.err.printf("下载失败: %s%n", e.getMessage());
            }
        }

        /**
         * 下载文件的一部分（使用合并后的请求头）
         */
        private void downloadPart() throws IOException, InterruptedException {
            // 创建请求构建器
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMinutes(5))
                    .GET();

            // 合并默认请求头和任务请求头（任务请求头优先）
            Map<String, List<String>> mergedHeaders = new HashMap<>(defaultHeaders);
            taskHeaders.forEach((key, values) -> {
                mergedHeaders.put(key, new ArrayList<>(values));
            });

            // 添加所有合并后的请求头
            mergedHeaders.forEach((key, values) -> {
                values.forEach(value -> requestBuilder.header(key, value));
            });

            // 发送请求并处理响应
            HttpResponse<InputStream> response = httpClient.send(
                    requestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.out.println(response);
                throw new IOException("下载失败，状态码: " + response.statusCode());
            }

            // 写入文件
            try (InputStream in = response.body();
                 FileChannel channel = FileChannel.open(tempPath, StandardOpenOption.WRITE)) {

                // 定位到起始位置
                channel.position(0);

                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    channel.write(java.nio.ByteBuffer.wrap(buffer, 0, bytesRead));
                }
            }
        }
    }

}