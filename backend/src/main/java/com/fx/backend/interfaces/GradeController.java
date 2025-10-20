package com.fx.backend.interfaces;

import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.jwt.RoleRequired;
import com.fx.backend.domain.entity.Grade;
import com.fx.backend.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@Validated
@RoleRequired({"ADMIN","TEACHER"})
@Tag(name = "成绩管理", description = "成绩录入/查询/修改")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
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
    @Operation(summary = "按学生查询成绩", description = "受时间段开放控制",
            responses = {
                    @ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "不在开放时间段")
            })
    public ResponseEntity<ApiResult<List<Grade>>> listByStudent(
            @PathVariable("studentId") @NotNull Long studentId,
            @RequestParam(value = "semester", required = false) String semester,
            @RequestParam(value = "academicYear", required = false) String academicYear
    ) {
        return ResponseEntity.ok(ApiResult.ok(gradeService.queryStudentGrades(studentId, semester, academicYear)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改成绩")
    public ResponseEntity<ApiResult<Void>> update(@PathVariable("id") Long id, @RequestBody Grade grade) {
        grade.setId(id);
        gradeService.updateGrade(grade);
        return ResponseEntity.ok(ApiResult.ok(null));
    }
}


