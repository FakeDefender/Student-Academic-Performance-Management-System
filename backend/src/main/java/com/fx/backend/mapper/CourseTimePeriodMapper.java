package com.fx.backend.mapper;

import com.fx.backend.domain.entity.CourseTimePeriod;
import com.fx.backend.domain.entity.CourseTimePeriodDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseTimePeriodMapper {
    int insert(CourseTimePeriod courseTimePeriod);
    int deleteById(@Param("id") Long id);
    List<CourseTimePeriod> findByCourseId(@Param("courseId") Long courseId);
    List<CourseTimePeriod> findByTimePeriodId(@Param("timePeriodId") Long timePeriodId);
    List<CourseTimePeriodDetail> findDetailsByTimePeriodId(@Param("timePeriodId") Long timePeriodId);
    boolean existsByCourseAndTimePeriod(@Param("courseId") Long courseId, @Param("timePeriodId") Long timePeriodId);
    List<Long> findTimePeriodIdsByCourseId(@Param("courseId") Long courseId);
    boolean isCourseTimePeriodActive(@Param("courseId") Long courseId);
}
