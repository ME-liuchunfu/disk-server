package com.spring.boot.utils.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

/**
 * 文件信息工具类，用于获取指定目录下的所有文件信息
 */
@Slf4j
public class FileInfoUtil {

    /**
     * 获取指定目录下的所有文件信息
     *
     * @param directoryPath 指定目录的路径
     * @return 包含所有文件信息的FileInfo列表
     * @throws IOException 如果发生I/O错误
     */
    public static List<FileInfo> getAllFileInfos(String directoryPath) throws IOException {
        File directory = new File(directoryPath);

        // 检查目录是否存在且是一个目录
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("指定路径不存在或不是一个目录: " + directoryPath);
        }

        List<FileInfo> fileInfos = new ArrayList<>();
        Path rootPath = directory.toPath();

        // 使用Files.walkFileTree遍历目录下的所有文件
        Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.toFile().isHidden()) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                if (StringUtils.startsWithIgnoreCase(dir.toFile().getName(), ".")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 只处理文件，不处理目录
                if (attrs.isRegularFile()) {
                    try {
                        log.info("正在处理:{}", file.getFileName());
                        FileInfo fileInfo = createFileInfo(rootPath, file);
                        fileInfos.add(fileInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // 处理文件访问失败的情况
                System.err.println("无法访问文件: " + file + ", 错误: " + exc.getMessage());
                return FileVisitResult.CONTINUE;
            }
        });

        return fileInfos;
    }

    /**
     * 为单个文件创建FileInfo对象
     *
     * @param rootPath 根目录路径
     * @param filePath 文件路径
     * @return 包含文件信息的FileInfo对象
     * @throws IOException 如果发生I/O错误
     */
    private static FileInfo createFileInfo(Path rootPath, Path filePath) throws Exception {
        File file = filePath.toFile();

        // 获取文件名
        String fileName = file.getName();

        // 获取相对路径
        String relativePath = rootPath.relativize(filePath).toString();

        // 获取文件类型（扩展名）
        String fileType = getFileExtension(fileName);

        // 获取文件大小
        long fileSize = file.length();

        // 计算文件哈希值
        String hashValue = calculateFileHash(file);

        return new FileInfo(fileName, relativePath, fileType, fileSize, hashValue);
    }

    /**
     * 获取文件的扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名，如果没有则返回空字符串
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 计算文件的SHA-256哈希值
     *
     * @param file 要计算哈希值的文件
     * @return 文件的SHA-256哈希值的十六进制字符串
     * @throws IOException 如果发生I/O错误
     */
    private static String calculateFileHash(File file) throws Exception {
        return FileHasher.calculateFileHash(file, FileHasher.Algorithm.SHA256);
    }

    public static FileInfo transferTo(Path basePath, MultipartFile inputs, String originalFilename, String hashValue) throws Exception {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileType(getFileExtension(originalFilename));
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String idStr = IdUtil.getSnowflakeNextIdStr();
        Path resolve = basePath.resolve(format).resolve(idStr + "." + fileInfo.getFileType());
        writeLargeFile(resolve, inputs.getBytes(), BUFFER_SIZE);
        fileInfo.setFileSize(resolve.toFile().length());
        fileInfo.setFileName(originalFilename);
        fileInfo.setRelativePath(resolve.toString().replace(basePath.toString(), ""));
        if (StrUtil.isBlank(hashValue)) {
            hashValue = calculateFileHash(resolve.toFile());
        }
        fileInfo.setRelativePath(fileInfo.getRelativePath().replace("\\", "/"));
        fileInfo.setHashValue(hashValue);
        return fileInfo;
    }

    public static FileInfo transferTo(Path basePath, File inputs, String originalFilename, String hashValue) throws Exception {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileType(getFileExtension(originalFilename));
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String idStr = IdUtil.getSnowflakeNextIdStr();
        Path resolve = basePath.resolve(format).resolve(idStr + "." + fileInfo.getFileType());
        FileUtil.copy(inputs, resolve.toFile(), true);
        fileInfo.setFileSize(resolve.toFile().length());
        fileInfo.setFileName(originalFilename);
        fileInfo.setRelativePath(resolve.toString().replace(basePath.toString(), ""));
        if (StrUtil.isBlank(hashValue)) {
            hashValue = calculateFileHash(resolve.toFile());
        }
        fileInfo.setRelativePath(fileInfo.getRelativePath().replace("\\", "/"));
        fileInfo.setHashValue(hashValue);
        return fileInfo;
    }

    private static final int BUFFER_SIZE = 4 * 1024 * 1024; // 4MB

    /**
     * 写入大文件
     * @param path 目标文件路径
     * @param data 要写入的数据（字节数组）
     * @throws IOException IO异常
     */
    public static void writeLargeFile(Path path, byte[] data, int buff) throws IOException {
        if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        // 打开文件通道（如果文件不存在则创建，存在则覆盖）
        try (FileChannel channel = (FileChannel) Files.newByteChannel(
                path,
                EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
        )) {
            // 创建缓冲区（直接内存缓冲区，减少JVM堆内存占用，适合大文件）
            ByteBuffer buffer = ByteBuffer.allocateDirect(buff);

            int offset = 0; // 数据偏移量
            int remaining = data.length; // 剩余待写入数据长度

            // 循环写入数据，直到所有数据写完
            while (remaining > 0) {
                // 计算本次写入的字节数（不超过缓冲区大小）
                int bytesToWrite = Math.min(remaining, buffer.capacity());

                // 将数据写入缓冲区
                buffer.put(data, offset, bytesToWrite);
                buffer.flip(); // 切换为读模式

                // 将缓冲区数据写入通道
                channel.write(buffer);

                // 重置缓冲区（清空已写入数据，准备下次写入）
                buffer.clear();

                // 更新偏移量和剩余数据长度
                offset += bytesToWrite;
                remaining -= bytesToWrite;
            }

            log.info("文件写入完成，路径：" + path.toAbsolutePath() + "，大小：" + data.length + "字节");
        }
    }

}
