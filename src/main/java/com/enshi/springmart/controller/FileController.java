package com.enshi.springmart.controller;

import com.enshi.springmart.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    /** 允许的图片类型 */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * 上传单个文件，返回可访问的 URL
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error("文件大小不能超过 10MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            return Result.error("不支持的图片格式，仅允许: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        try {
            String url = saveFile(file, extension);
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            log.info("文件上传成功: {} -> {}", originalFilename, url);
            return Result.success("上传成功", data);
        } catch (IOException e) {
            log.error("文件保存失败", e);
            return Result.error("文件保存失败，请稍后重试");
        }
    }

    /**
     * 批量上传，最多 9 张
     */
    @PostMapping("/upload/batch")
    public Result<List<Map<String, String>>> uploadBatch(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Result.error("请至少选择一个文件");
        }
        if (files.length > 9) {
            return Result.error("单次最多上传 9 张图片");
        }

        List<Map<String, String>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                return Result.error("文件 [" + originalFilename + "] 格式不支持");
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                return Result.error("文件 [" + originalFilename + "] 超过 10MB 限制");
            }

            try {
                String url = saveFile(file, extension);
                Map<String, String> item = new HashMap<>();
                item.put("name", originalFilename);
                item.put("url", url);
                results.add(item);
            } catch (IOException e) {
                log.error("批量上传时文件保存失败: {}", originalFilename, e);
                return Result.error("文件 [" + originalFilename + "] 保存失败");
            }
        }

        log.info("批量上传成功，共 {} 个文件", results.size());
        return Result.success("全部上传成功", results);
    }

    // ==================== 内部方法 ====================

    private String saveFile(MultipartFile file, String extension) throws IOException {
        // 按日期分目录，避免单个目录文件过多
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newFilename = UUID.randomUUID().toString() + "." + extension.toLowerCase();

        Path dir = Paths.get(uploadPath, dateDir);
        Files.createDirectories(dir);

        Path dest = dir.resolve(newFilename);
        file.transferTo(dest);

        return "/uploads/" + dateDir + "/" + newFilename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return null;
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
