package com.fx.backend.service;

import com.fx.backend.common.exception.BusinessException;
import com.fx.backend.domain.entity.Teacher;
import com.fx.backend.mapper.GradeMapper;
import com.fx.backend.mapper.TeacherMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import com.fx.backend.common.api.PageRequest;
import com.fx.backend.common.api.PageResponse;

@Service
public class TeacherService {
    private final TeacherMapper teacherMapper;
    private final GradeMapper gradeMapper;

    public TeacherService(TeacherMapper teacherMapper, GradeMapper gradeMapper) {
        this.teacherMapper = teacherMapper;
        this.gradeMapper = gradeMapper;
    }

    @Transactional
    public Long create(Teacher t) { teacherMapper.insert(t); return t.getId(); }

    public Optional<Teacher> findById(Long id) { return teacherMapper.findById(id); }

    @Transactional
    public void update(Teacher t) { teacherMapper.update(t); }

    @Transactional
    public void delete(Long id) {
        int refs = gradeMapper.countByTeacherId(id);
        if (refs > 0) {
            throw new BusinessException("DELETE_CONFLICT", "该教师存在成绩记录，无法删除");
        }
        teacherMapper.deleteById(id);
    }

    public PageResponse<Teacher> page(String keyword, String department, PageRequest pr) {
        List<Teacher> list = teacherMapper.page(keyword, department, pr.offset(), pr.getPageSize());
        long total = teacherMapper.count(keyword, department);
        return PageResponse.of(total, pr.getPageNo(), pr.getPageSize(), list);
    }
}


