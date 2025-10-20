package com.fx.backend.mapper;

import com.fx.backend.domain.entity.Student;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

public interface StudentMapper {
    int insert(Student student);
    Optional<Student> findById(@Param("id") Long id);
    Optional<Student> findByStudentId(@Param("studentId") String studentId);
    int update(Student s);
    int deleteById(@Param("id") Long id);

    java.util.List<Student> page(@Param("keyword") String keyword,
                                 @Param("status") String status,
                                 @Param("offset") int offset,
                                 @Param("pageSize") int pageSize);
    long count(@Param("keyword") String keyword,
               @Param("status") String status);
}


