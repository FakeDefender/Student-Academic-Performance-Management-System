package com.fx.backend.mapper;

import com.fx.backend.domain.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

public interface CourseMapper {
    int insert(Course course);
    Optional<Course> findById(@Param("id") Long id);
    Optional<Course> findByCourseCode(@Param("courseCode") String courseCode);
    int update(Course course);
    int deleteById(@Param("id") Long id);

    java.util.List<Course> page(@Param("keyword") String keyword,
                                @Param("department") String department,
                                @Param("isActive") Boolean isActive,
                                @Param("offset") int offset,
                                @Param("pageSize") int pageSize);
    long count(@Param("keyword") String keyword,
               @Param("department") String department,
               @Param("isActive") Boolean isActive);
}


