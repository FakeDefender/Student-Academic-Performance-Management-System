package com.fx.backend.interfaces;

import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.jwt.RoleRequired;
import com.fx.backend.service.BatchImportService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RoleRequired({"ADMIN","TEACHER"})
public class FileController {
    private final BatchImportService batchImportService;

    public FileController(BatchImportService batchImportService) { this.batchImportService = batchImportService; }

    @PostMapping("/grades/import")
    public ResponseEntity<ApiResult<Integer>> importGrades(
            @RequestParam("file") MultipartFile file,
            @RequestParam("semester") String semester,
            @RequestParam("academicYear") String academicYear,
            @RequestParam("teacherId") Long teacherId
    ) {
        int success = batchImportService.importGrades(file, semester, academicYear, teacherId);
        return ResponseEntity.ok(ApiResult.ok(success));
    }
}


