package com.fx.backend.interfaces;

import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.api.PageRequest;
import com.fx.backend.common.api.PageResponse;
import com.fx.backend.domain.entity.Course;
import com.fx.backend.service.CourseService;

import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "课程管理", description = "课程信息的增删改查")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) { this.courseService = courseService; }

    @PostMapping
    @Operation(summary = "新增课程")
    public ResponseEntity<ApiResult<Long>> create(@RequestBody Course c) { return ResponseEntity.ok(ApiResult.ok(courseService.create(c))); }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取课程")
    public ResponseEntity<ApiResult<Optional<Course>>> get(@PathVariable Long id) { return ResponseEntity.ok(ApiResult.ok(courseService.findById(id))); }

    @PutMapping("/{id}")
    @Operation(summary = "更新课程")
    public ResponseEntity<ApiResult<Void>> update(@PathVariable Long id, @RequestBody Course c) { c.setId(id); courseService.update(c); return ResponseEntity.ok(ApiResult.ok(null)); }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程")
    public ResponseEntity<ApiResult<Void>> delete(@PathVariable Long id) { courseService.delete(id); return ResponseEntity.ok(ApiResult.ok(null)); }

    @GetMapping
    @Operation(summary = "分页查询课程")
    public ResponseEntity<ApiResult<PageResponse<Course>>> page(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            PageRequest pr
    ) {
        return ResponseEntity.ok(ApiResult.ok(courseService.page(keyword, department, isActive, pr)));
    }
}


