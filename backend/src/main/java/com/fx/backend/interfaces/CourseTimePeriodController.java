package com.fx.backend.interfaces;

import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.jwt.RoleRequired;
import com.fx.backend.domain.entity.CourseTimePeriod;
import com.fx.backend.domain.entity.CourseTimePeriodDetail;
import com.fx.backend.service.CourseTimePeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-time-periods")
@RoleRequired({"ADMIN", "TEACHER"})
@Tag(name = "课程时间段管理", description = "管理课程与时间段的关联关系")
public class CourseTimePeriodController {
    private final CourseTimePeriodService courseTimePeriodService;

    public CourseTimePeriodController(CourseTimePeriodService courseTimePeriodService) {
        this.courseTimePeriodService = courseTimePeriodService;
    }

    @PostMapping
    @Operation(summary = "关联课程与时间段")
    public ResponseEntity<ApiResult<Long>> create(@RequestBody CourseTimePeriod courseTimePeriod) {
        Long id = courseTimePeriodService.create(courseTimePeriod);
        return ResponseEntity.ok(ApiResult.ok(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "取消课程与时间段的关联")
    public ResponseEntity<ApiResult<Void>> delete(@PathVariable Long id) {
        courseTimePeriodService.delete(id);
        return ResponseEntity.ok(ApiResult.ok(null));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程关联的时间段")
    public ResponseEntity<ApiResult<List<CourseTimePeriod>>> getByCourseId(@PathVariable Long courseId) {
        List<CourseTimePeriod> list = courseTimePeriodService.findByCourseId(courseId);
        return ResponseEntity.ok(ApiResult.ok(list));
    }

    @GetMapping("/time-period/{timePeriodId}")
    @Operation(summary = "获取时间段关联的课程")
    public ResponseEntity<ApiResult<List<CourseTimePeriodDetail>>> getByTimePeriodId(@PathVariable Long timePeriodId) {
        List<CourseTimePeriodDetail> list = courseTimePeriodService.findDetailsByTimePeriodId(timePeriodId);
        return ResponseEntity.ok(ApiResult.ok(list));
    }

    @GetMapping("/course/{courseId}/active")
    @Operation(summary = "检查课程是否在开放时间段内")
    public ResponseEntity<ApiResult<Boolean>> isCourseTimePeriodActive(@PathVariable Long courseId) {
        boolean isActive = courseTimePeriodService.isCourseTimePeriodActive(courseId);
        return ResponseEntity.ok(ApiResult.ok(isActive));
    }
}
