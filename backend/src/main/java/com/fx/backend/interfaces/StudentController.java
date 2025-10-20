package com.fx.backend.interfaces;

import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.jwt.RoleRequired;
import com.fx.backend.common.api.PageRequest;
import com.fx.backend.common.api.PageResponse;
import com.fx.backend.domain.entity.Student;
import com.fx.backend.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@RoleRequired({"ADMIN"})
@Tag(name = "学生管理", description = "学生信息的增删改查")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) { this.studentService = studentService; }

    @PostMapping
    @Operation(summary = "新增学生")
    public ResponseEntity<ApiResult<Long>> create(@RequestBody Student s) { return ResponseEntity.ok(ApiResult.ok(studentService.create(s))); }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取学生")
    public ResponseEntity<ApiResult<Optional<Student>>> get(@PathVariable Long id) { return ResponseEntity.ok(ApiResult.ok(studentService.findById(id))); }

    @PutMapping("/{id}")
    @Operation(summary = "更新学生")
    public ResponseEntity<ApiResult<Void>> update(@PathVariable Long id, @RequestBody Student s) { s.setId(id); studentService.update(s); return ResponseEntity.ok(ApiResult.ok(null)); }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生")
    public ResponseEntity<ApiResult<Void>> delete(@PathVariable Long id) { studentService.delete(id); return ResponseEntity.ok(ApiResult.ok(null)); }

    @GetMapping
    @Operation(summary = "分页查询学生")
    public ResponseEntity<ApiResult<PageResponse<Student>>> page(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            PageRequest pr
    ) {
        return ResponseEntity.ok(ApiResult.ok(studentService.page(keyword, status, pr)));
    }
}


