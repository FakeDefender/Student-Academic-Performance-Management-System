package com.fx.backend.interfaces;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.jwt.RoleRequired;
import com.fx.backend.service.BatchImportService;

@RestController
@RequestMapping("/api/files")
@RoleRequired({"ADMIN","TEACHER"})
public class FileController {
    private final BatchImportService batchImportService;

    public FileController(BatchImportService batchImportService) { this.batchImportService = batchImportService; }

    @PostMapping("/grades/import")
    public ResponseEntity<ApiResult<Integer>> importGrades(
            @RequestParam("file") MultipartFile file
    ) {
        int success = batchImportService.importGradesFromFile(file);
        return ResponseEntity.ok(ApiResult.ok(success));
    }

    @GetMapping("/grades/template")
    public ResponseEntity<Resource> downloadTemplate(@RequestParam("type") String type) throws IOException {
        String fileName;
        String contentType;
        
        if ("csv".equalsIgnoreCase(type)) {
            fileName = "成绩导入模板.csv";
            contentType = "text/csv";
        } else {
            fileName = "成绩导入模板.xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        
        Resource resource = new ClassPathResource("templates/" + fileName);
        
        // 对中文文件名进行URL编码，避免HTTP响应头编码问题
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // 将+替换为%20以符合标准
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .body(resource);
    }
}


