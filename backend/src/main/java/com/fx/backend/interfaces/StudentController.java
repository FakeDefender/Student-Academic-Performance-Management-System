package com.fx.backend.interfaces;

import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.jwt.RoleRequired;
import com.fx.backend.common.jwt.CurrentUser;
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
    private final CurrentUser currentUser;

    public StudentController(StudentService studentService, CurrentUser currentUser) { 
        this.studentService = studentService; 
        this.currentUser = currentUser;
    }

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

    @GetMapping("/profile")
    @RoleRequired({"STUDENT"})
    @Operation(summary = "获取当前学生个人信息")
    public ResponseEntity<ApiResult<Student>> getProfile() {
        String userId = currentUser.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResult.error("AUTH_FAIL", "未找到用户信息"));
        }
        
        // 通过userId查找学生信息
        Student student = studentService.findByUserId(Long.parseLong(userId));
        if (student == null) {
            return ResponseEntity.ok(ApiResult.error("STUDENT_NOT_FOUND", "学生信息不存在"));
        }
        return ResponseEntity.ok(ApiResult.ok(student));
    }

    @PutMapping("/profile")
    @RoleRequired({"STUDENT"})
    @Operation(summary = "更新当前学生个人信息")
    public ResponseEntity<ApiResult<Void>> updateProfile(@RequestBody Student student) {
        String userId = currentUser.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResult.error("AUTH_FAIL", "未找到用户信息"));
        }
        
        // 获取当前学生信息
        Student currentStudent = studentService.findByUserId(Long.parseLong(userId));
        if (currentStudent == null) {
            return ResponseEntity.ok(ApiResult.error("STUDENT_NOT_FOUND", "学生信息不存在"));
        }
        
        // 学号不允许修改，保持原有学号
        student.setStudentId(currentStudent.getStudentId());
        student.setId(currentStudent.getId());
        
        studentService.update(student);
        return ResponseEntity.ok(ApiResult.ok(null));
    }
}


