package com.fx.backend.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.jwt.CurrentUser;
import com.fx.backend.common.jwt.RoleRequired;
import com.fx.backend.common.ratelimit.RateLimit;
import com.fx.backend.domain.entity.Grade;
import com.fx.backend.domain.entity.GradeWithCourseInfo;
import com.fx.backend.service.GradeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/grades")
@Validated
@RoleRequired({"ADMIN","TEACHER"})
@Tag(name = "成绩管理", description = "成绩录入/查询/修改")
public class GradeController {

    private final GradeService gradeService;
    private final CurrentUser currentUser;

    public GradeController(GradeService gradeService, CurrentUser currentUser) {
        this.gradeService = gradeService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @Operation(summary = "录入成绩", responses = {
            @ApiResponse(responseCode = "200", description = "成功，返回ID"),
            @ApiResponse(responseCode = "400", description = "参数错误/逻辑外键校验失败")
    })
    public ResponseEntity<ApiResult<Long>> create(@RequestBody Grade grade) {
        Long id = gradeService.createGrade(grade);
        return ResponseEntity.ok(ApiResult.ok(id));
    }

    @GetMapping("/student/{studentId}")
    @RoleRequired({"ADMIN","TEACHER","STUDENT"})
    @RateLimit(keyPrefix = "grade_query", maxRequests = 60, timeWindow = 60) // 每分钟最多60次查询
    @Operation(summary = "按学生ID查询成绩", description = "受时间段开放控制，支持缓存和限流",
            responses = {
                    @ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "不在开放时间段"),
                    @ApiResponse(responseCode = "429", description = "请求过于频繁")
            })
        public ResponseEntity<ApiResult<List<GradeWithCourseInfo>>> listByStudent(
                @PathVariable("studentId") @NotNull Long studentId,
                @RequestParam(value = "semester", required = false) String semester,
                @RequestParam(value = "academicYear", required = false) String academicYear
        ) {
            return ResponseEntity.ok(ApiResult.ok(gradeService.queryStudentGrades(studentId, semester, academicYear)));
        }

    @GetMapping("/student/by-number/{studentNumber}")
    @RoleRequired({"ADMIN","TEACHER","STUDENT"})
    @RateLimit(keyPrefix = "grade_query", maxRequests = 60, timeWindow = 60) // 每分钟最多60次查询
    @Operation(summary = "按学生学号查询成绩", description = "受时间段开放控制，支持缓存和限流",
            responses = {
                    @ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "不在开放时间段"),
                    @ApiResponse(responseCode = "429", description = "请求过于频繁")
            })
    public ResponseEntity<ApiResult<List<GradeWithCourseInfo>>> listByStudentNumber(
            @PathVariable("studentNumber") @NotNull String studentNumber,
            @RequestParam(value = "semester", required = false) String semester,
            @RequestParam(value = "academicYear", required = false) String academicYear
    ) {
        try {
            // 确保参数正确解码
            String decodedSemester = semester != null ? java.net.URLDecoder.decode(semester, "UTF-8") : null;
            String decodedAcademicYear = academicYear != null ? java.net.URLDecoder.decode(academicYear, "UTF-8") : null;
            
            return ResponseEntity.ok(ApiResult.ok(gradeService.queryStudentGradesByNumber(studentNumber, decodedSemester, decodedAcademicYear)));
        } catch (Exception e) {
            // 如果解码失败，使用原始参数
            return ResponseEntity.ok(ApiResult.ok(gradeService.queryStudentGradesByNumber(studentNumber, semester, academicYear)));
        }
    }

    @GetMapping("/teacher/student/{studentNumber}")
    @RoleRequired({"TEACHER"})
    @RateLimit(keyPrefix = "teacher_grade_query", maxRequests = 60, timeWindow = 60) // 每分钟最多60次查询
    @Operation(summary = "教师查询学生成绩", description = "教师只能查询自己课程的学生成绩，受时间段开放控制",
            responses = {
                    @ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "不在开放时间段/权限不足"),
                    @ApiResponse(responseCode = "429", description = "请求过于频繁")
            })
    public ResponseEntity<ApiResult<List<GradeWithCourseInfo>>> listByTeacherForStudent(
            @PathVariable("studentNumber") @NotNull String studentNumber,
            @RequestParam(value = "semester", required = false) String semester,
            @RequestParam(value = "academicYear", required = false) String academicYear
    ) {
        String currentUserId = currentUser.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.ok(ApiResult.error("AUTH_FAIL", "未找到用户信息"));
        }
        
        try {
            // 确保参数正确解码
            String decodedSemester = semester != null ? java.net.URLDecoder.decode(semester, "UTF-8") : null;
            String decodedAcademicYear = academicYear != null ? java.net.URLDecoder.decode(academicYear, "UTF-8") : null;
            
            Long teacherId = Long.parseLong(currentUserId);
            return ResponseEntity.ok(ApiResult.ok(gradeService.queryStudentGradesByTeacher(teacherId, studentNumber, decodedSemester, decodedAcademicYear)));
        } catch (Exception e) {
            // 如果解码失败，使用原始参数
            Long teacherId = Long.parseLong(currentUserId);
            return ResponseEntity.ok(ApiResult.ok(gradeService.queryStudentGradesByTeacher(teacherId, studentNumber, semester, academicYear)));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改成绩")
    public ResponseEntity<ApiResult<Void>> update(@PathVariable("id") Long id, @RequestBody Grade grade) {
        grade.setId(id);
        gradeService.updateGrade(grade);
        return ResponseEntity.ok(ApiResult.ok(null));
    }
}


