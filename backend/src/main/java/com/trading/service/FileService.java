package com.trading.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:http://localhost:8080/api/uploads}")
    private String urlPrefix;

    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!isAllowedExtension(extension)) {
            throw new RuntimeException("不支持的文件类型，仅支持: jpg, jpeg, png, gif, bmp, webp");
        }

        // 验证文件大小（限制10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("文件大小不能超过10MB");
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + extension;
        
        // 创建上传目录（按日期分类）
        String dateDir = java.time.LocalDate.now().toString();
        File uploadDir = new File(uploadPath + File.separator + dateDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 保存文件
        Path filePath = Paths.get(uploadDir.getAbsolutePath(), fileName);
        Files.write(filePath, file.getBytes());

        // 返回访问URL
        return urlPrefix + "/" + dateDir + "/" + fileName;
    }

    public boolean deleteFile(String fileUrl) {
        try {
            // 从URL中提取文件路径
            String relativePath = fileUrl.replace(urlPrefix + "/", "");
            File file = new File(uploadPath + File.separator + relativePath);
            if (file.exists()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot).toLowerCase();
    }

    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}

