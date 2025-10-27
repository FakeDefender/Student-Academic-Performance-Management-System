package com.fx.backend.interfaces;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.api.PageRequest;
import com.fx.backend.common.api.PageResponse;
import com.fx.backend.common.jwt.CurrentUser;
import com.fx.backend.common.jwt.RoleRequired;
import com.fx.backend.domain.entity.Course;
import com.fx.backend.domain.entity.StudentCourseInfo;
import com.fx.backend.mapper.TeacherMapper;
import com.fx.backend.service.CourseService;
import com.fx.backend.service.GradeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "课程管理", description = "课程信息的增删改查")
public class CourseController {
    private final CourseService courseService;
    private final CurrentUser currentUser;
    private final TeacherMapper teacherMapper;
    private final GradeService gradeService;

    public CourseController(CourseService courseService, CurrentUser currentUser, TeacherMapper teacherMapper, GradeService gradeService) { 
        this.courseService = courseService; 
        this.currentUser = currentUser;
        this.teacherMapper = teacherMapper;
        this.gradeService = gradeService;
    }

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

    @GetMapping("/my-courses")
    @RoleRequired({"TEACHER"})
    @Operation(summary = "获取教师教授的课程")
    public ResponseEntity<ApiResult<java.util.List<Course>>> getMyCourses() {
        String userId = currentUser.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResult.error("AUTH_FAIL", "未找到用户信息"));
        }
        Long userIdLong = Long.valueOf(userId);
        var teacherOpt = teacherMapper.findByUserId(userIdLong);
        if (teacherOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResult.error("NOT_FOUND", "未找到教师信息"));
        }
        Long teacherId = teacherOpt.get().getId();
        return ResponseEntity.ok(ApiResult.ok(courseService.findByTeacherId(teacherId)));
    }

    @GetMapping("/{courseId}/students")
    @RoleRequired({"TEACHER"})
    @Operation(summary = "获取课程的所有学生")
    public ResponseEntity<ApiResult<java.util.List<StudentCourseInfo>>> getCourseStudents(@PathVariable Long courseId) {
        String userId = currentUser.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResult.error("AUTH_FAIL", "未找到用户信息"));
        }
        Long userIdLong = Long.valueOf(userId);
        var teacherOpt = teacherMapper.findByUserId(userIdLong);
        if (teacherOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResult.error("NOT_FOUND", "未找到教师信息"));
        }
        Long teacherId = teacherOpt.get().getId();
        
        // 检查教师是否有权限查看该课程的学生
        if (!gradeService.hasTeacherPermissionForCourse(teacherId, courseId)) {
            return ResponseEntity.ok(ApiResult.error("PERMISSION_DENIED", "您没有权限查看该课程的学生"));
        }
        
        java.util.List<StudentCourseInfo> students = gradeService.getStudentsByCourse(courseId);
        return ResponseEntity.ok(ApiResult.ok(students));
    }
}


