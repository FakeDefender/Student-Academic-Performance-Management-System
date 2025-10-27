package com.fx.backend.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;

import com.fx.backend.domain.entity.Grade;
import com.fx.backend.domain.entity.GradeWithCourseInfo;
import com.fx.backend.domain.entity.StudentCourseInfo;

public interface GradeMapper {
    int insert(Grade grade);
    int update(Grade grade);
    Optional<Grade> findById(@Param("id") Long id);
    List<Grade> findByStudent(@Param("studentId") Long studentId,
                              @Param("semester") String semester,
                              @Param("academicYear") String academicYear);
    
    // 查询带课程信息的学生成绩
    List<GradeWithCourseInfo> findByStudentWithCourseInfo(@Param("studentId") Long studentId,
                                                          @Param("semester") String semester,
                                                          @Param("academicYear") String academicYear);

    // 查找重复成绩（学生+课程+学期+学年）
    Optional<Grade> findByStudentCourseSemesterYear(@Param("studentId") Long studentId,
                                                   @Param("courseId") Long courseId,
                                                   @Param("semester") String semester,
                                                   @Param("academicYear") String academicYear);

    // 查询课程的所有学生（带学生信息）
    List<StudentCourseInfo> findStudentsByCourse(@Param("courseId") Long courseId);

    // 引用计数用于删除保护（逻辑外键）
    int countByStudentId(@Param("studentId") Long studentId);
    int countByCourseId(@Param("courseId") Long courseId);
    int countByTeacherId(@Param("teacherId") Long teacherId);
}


