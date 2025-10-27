package com.fx.backend.service;

import com.fx.backend.common.exception.BusinessException;
import com.fx.backend.domain.entity.Student;
import com.fx.backend.mapper.GradeMapper;
import com.fx.backend.mapper.StudentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import com.fx.backend.common.api.PageRequest;
import com.fx.backend.common.api.PageResponse;

@Service
public class StudentService {
    private final StudentMapper studentMapper;
    private final GradeMapper gradeMapper;

    public StudentService(StudentMapper studentMapper, GradeMapper gradeMapper) {
        this.studentMapper = studentMapper;
        this.gradeMapper = gradeMapper;
    }

    @Transactional
    public Long create(Student s) {
        studentMapper.insert(s);
        return s.getId();
    }

    public Optional<Student> findById(Long id) { return studentMapper.findById(id); }

    @Transactional
    public void update(Student s) { studentMapper.update(s); }

    @Transactional
    public void delete(Long id) {
        int refs = gradeMapper.countByStudentId(id);
        if (refs > 0) {
            throw new BusinessException("DELETE_CONFLICT", "该学生存在成绩记录，无法删除");
        }
        studentMapper.deleteById(id);
    }

    public PageResponse<Student> page(String keyword, String status, PageRequest pr) {
        List<Student> list = studentMapper.page(keyword, status, pr.offset(), pr.getPageSize());
        long total = studentMapper.count(keyword, status);
        return PageResponse.of(total, pr.getPageNo(), pr.getPageSize(), list);
    }

    public Student findByStudentId(String studentId) {
        return studentMapper.findByStudentId(studentId).orElse(null);
    }

    public Student findByUserId(Long userId) {
        return studentMapper.findByUserId(userId).orElse(null);
    }

    public void updateProfile(Student student) {
        Student existing = findByStudentId(student.getStudentId());
        if (existing == null) {
            throw new BusinessException("STUDENT_NOT_FOUND", "学生不存在");
        }
        existing.setName(student.getName());
        existing.setClassName(student.getClassName());
        existing.setMajor(student.getMajor());
        existing.setEnrollmentDate(student.getEnrollmentDate());
        existing.setGraduationDate(student.getGraduationDate());
        existing.setStatus(student.getStatus());
        studentMapper.update(existing);
    }
}

