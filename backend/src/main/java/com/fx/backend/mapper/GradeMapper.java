package com.fx.backend.mapper;

import com.fx.backend.domain.entity.Grade;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface GradeMapper {
    int insert(Grade grade);
    int update(Grade grade);
    Optional<Grade> findById(@Param("id") Long id);
    List<Grade> findByStudent(@Param("studentId") Long studentId,
                              @Param("semester") String semester,
                              @Param("academicYear") String academicYear);

    // 引用计数用于删除保护（逻辑外键）
    int countByStudentId(@Param("studentId") Long studentId);
    int countByCourseId(@Param("courseId") Long courseId);
    int countByTeacherId(@Param("teacherId") Long teacherId);
}


