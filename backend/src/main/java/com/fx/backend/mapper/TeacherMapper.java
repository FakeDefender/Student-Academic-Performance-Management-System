package com.fx.backend.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Param;

import com.fx.backend.domain.entity.Teacher;

public interface TeacherMapper {
    int insert(Teacher teacher);
    Optional<Teacher> findById(@Param("id") Long id);
    Optional<Teacher> findByTeacherId(@Param("teacherId") String teacherId);
    Optional<Teacher> findByUserId(@Param("userId") Long userId);
    Optional<Teacher> findByTeacherNumber(@Param("teacherNumber") String teacherNumber);
    int update(Teacher t);
    int deleteById(@Param("id") Long id);

    java.util.List<Teacher> page(@Param("keyword") String keyword,
                                 @Param("department") String department,
                                 @Param("offset") int offset,
                                 @Param("pageSize") int pageSize);
    long count(@Param("keyword") String keyword,
               @Param("department") String department);
}


