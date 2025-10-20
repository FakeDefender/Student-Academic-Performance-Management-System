package com.fx.backend.interfaces;

import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.jwt.RoleRequired;
import com.fx.backend.common.api.PageRequest;
import com.fx.backend.common.api.PageResponse;
import com.fx.backend.domain.entity.Teacher;
import com.fx.backend.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/teachers")
@RoleRequired({"ADMIN"})
@Tag(name = "教师管理", description = "教师信息的增删改查")
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) { this.teacherService = teacherService; }

    @PostMapping
    @Operation(summary = "新增教师")
    public ResponseEntity<ApiResult<Long>> create(@RequestBody Teacher t) { return ResponseEntity.ok(ApiResult.ok(teacherService.create(t))); }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取教师")
    public ResponseEntity<ApiResult<Optional<Teacher>>> get(@PathVariable Long id) { return ResponseEntity.ok(ApiResult.ok(teacherService.findById(id))); }

    @PutMapping("/{id}")
    @Operation(summary = "更新教师")
    public ResponseEntity<ApiResult<Void>> update(@PathVariable Long id, @RequestBody Teacher t) { t.setId(id); teacherService.update(t); return ResponseEntity.ok(ApiResult.ok(null)); }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除教师")
    public ResponseEntity<ApiResult<Void>> delete(@PathVariable Long id) { teacherService.delete(id); return ResponseEntity.ok(ApiResult.ok(null)); }

    @GetMapping
    @Operation(summary = "分页查询教师")
    public ResponseEntity<ApiResult<PageResponse<Teacher>>> page(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "department", required = false) String department,
            PageRequest pr
    ) {
        return ResponseEntity.ok(ApiResult.ok(teacherService.page(keyword, department, pr)));
    }
}


