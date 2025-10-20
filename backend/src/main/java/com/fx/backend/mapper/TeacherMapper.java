package com.fx.backend.mapper;

import com.fx.backend.domain.entity.Teacher;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

public interface TeacherMapper {
    int insert(Teacher teacher);
    Optional<Teacher> findById(@Param("id") Long id);
    Optional<Teacher> findByTeacherId(@Param("teacherId") String teacherId);
    int update(Teacher t);
    int deleteById(@Param("id") Long id);

    java.util.List<Teacher> page(@Param("keyword") String keyword,
                                 @Param("department") String department,
                                 @Param("offset") int offset,
                                 @Param("pageSize") int pageSize);
    long count(@Param("keyword") String keyword,
               @Param("department") String department);
}


