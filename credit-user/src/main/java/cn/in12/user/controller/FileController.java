package cn.in12.user.controller;

import cn.in12.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "文件上传")
@RestController
@RequestMapping("/user")
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private Path getUploadPath() {
        return Paths.get(System.getProperty("user.dir"), uploadDir).toAbsolutePath();
    }

    @Operation(summary = "上传图片文件")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error("文件名不能为空");
        }

        String ext = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            ext = originalFilename.substring(dotIndex);
        }

        String newFilename = UUID.randomUUID().toString().replace("-", "") + ext;

        try {
            Path uploadPath = getUploadPath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            File dest = uploadPath.resolve(newFilename).toFile();
            file.transferTo(dest);

            String url = "/uploads/" + newFilename;
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            data.put("filename", newFilename);
            return Result.success(data);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除已上传的图片")
    @DeleteMapping("/file/{filename}")
    public Result<String> deleteFile(@PathVariable("filename") String filename) {
        try {
            Path uploadPath = getUploadPath();
            Path filePath = uploadPath.resolve(filename).normalize();

            if (!filePath.startsWith(uploadPath)) {
                return Result.error("非法路径");
            }

            File file = filePath.toFile();
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    return Result.success("删除成功");
                } else {
                    return Result.error("删除失败");
                }
            } else {
                return Result.success("文件不存在");
            }
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
