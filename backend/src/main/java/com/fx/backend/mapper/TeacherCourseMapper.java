package com.fx.backend.mapper;

import com.fx.backend.domain.entity.TeacherCourse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeacherCourseMapper {
    int insert(TeacherCourse teacherCourse);
    int deleteById(@Param("id") Long id);
    List<TeacherCourse> findByTeacherId(@Param("teacherId") Long teacherId);
    List<TeacherCourse> findByCourseId(@Param("courseId") Long courseId);
    boolean existsByTeacherAndCourse(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);
    List<Long> findCourseIdsByTeacherId(@Param("teacherId") Long teacherId);
    List<Long> findStudentIdsByTeacherId(@Param("teacherId") Long teacherId);
}
