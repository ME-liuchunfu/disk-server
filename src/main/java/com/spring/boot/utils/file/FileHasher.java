package com.spring.boot.utils.file;

/**
 * description: TODO
 *
 * @author spring
 * date 2025/11/10 13:15
 * version 1.0
 */
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHasher {

    // 支持的哈希算法
    public enum Algorithm {
        MD5, SHA1, SHA256
    }

    /**
     * 计算文件的哈希值
     * @param file 要计算哈希值的文件
     * @param algorithm 哈希算法
     * @return 文件的哈希值（十六进制字符串）
     * @throws IOException 如果文件读取失败
     * @throws NoSuchAlgorithmException 如果不支持指定的哈希算法
     */
    public static String calculateFileHash(File file, Algorithm algorithm)
            throws IOException, NoSuchAlgorithmException {

        // 验证文件是否存在且可读取
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new IOException("文件不存在、不是普通文件或无法读取: " + file.getAbsolutePath());
        }

        // 获取消息摘要实例
        MessageDigest digest = MessageDigest.getInstance(algorithm.name());

        // 读取文件内容并更新哈希计算
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192]; // 8KB缓冲区
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        // 完成哈希计算并转换为十六进制字符串
        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    /**
     * 计算文件的哈希值
     * @param file 要计算哈希值的文件
     * @param algorithm 哈希算法
     * @return 文件的哈希值（十六进制字符串）
     * @throws IOException 如果文件读取失败
     * @throws NoSuchAlgorithmException 如果不支持指定的哈希算法
     */
    public static String calculateFileHash(MultipartFile file, Algorithm algorithm)
            throws IOException, NoSuchAlgorithmException {
        // 获取消息摘要实例
        MessageDigest digest = MessageDigest.getInstance(algorithm.name());

        // 读取文件内容并更新哈希计算
        try (BufferedInputStream fis = new BufferedInputStream(file.getInputStream())) {
            byte[] buffer = new byte[8192]; // 8KB缓冲区
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        // 完成哈希计算并转换为十六进制字符串
        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    /**
     * 将字节数组转换为十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * 检查两个文件是否相同（通过比较哈希值）
     */
    public static boolean areFilesEqual(File file1, File file2) throws IOException, NoSuchAlgorithmException {
        // 先比较文件大小，快速排除不同的文件
        if (file1.length() != file2.length()) {
            return false;
        }

        // 计算并比较哈希值
        String hash1 = calculateFileHash(file1, Algorithm.SHA256);
        String hash2 = calculateFileHash(file2, Algorithm.SHA256);

        return hash1.equals(hash2);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("用法: java FileHasher <文件路径>");
            return;
        }

        File file = new File(args[0]);

        try {
            // 计算SHA-256哈希值
            String sha256Hash = calculateFileHash(file, Algorithm.SHA256);
            System.out.println("SHA-256哈希值: " + sha256Hash);

            // 可以同时计算其他算法的哈希值进行对比
            String md5Hash = calculateFileHash(file, Algorithm.MD5);
            System.out.println("MD5哈希值: " + md5Hash);

        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("计算哈希值失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

