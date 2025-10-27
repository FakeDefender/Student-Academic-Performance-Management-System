package com.fx.backend.service;

import com.fx.backend.common.exception.BusinessException;
import com.fx.backend.domain.entity.Course;
import com.fx.backend.mapper.CourseMapper;
import com.fx.backend.mapper.GradeMapper;
import com.fx.backend.mapper.TeacherCourseMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import com.fx.backend.common.api.PageRequest;
import com.fx.backend.common.api.PageResponse;

@Service
public class CourseService {
    private final CourseMapper courseMapper;
    private final GradeMapper gradeMapper;
    private final TeacherCourseMapper teacherCourseMapper;

    public CourseService(CourseMapper courseMapper, GradeMapper gradeMapper, TeacherCourseMapper teacherCourseMapper) {
        this.courseMapper = courseMapper;
        this.gradeMapper = gradeMapper;
        this.teacherCourseMapper = teacherCourseMapper;
    }

    @Transactional
    public Long create(Course c) { courseMapper.insert(c); return c.getId(); }

    public Optional<Course> findById(Long id) { return courseMapper.findById(id); }

    @Transactional
    public void update(Course c) { courseMapper.update(c); }

    @Transactional
    public void delete(Long id) {
        int refs = gradeMapper.countByCourseId(id);
        if (refs > 0) {
            throw new BusinessException("DELETE_CONFLICT", "该课程存在成绩记录，无法删除");
        }
        courseMapper.deleteById(id);
    }

    public PageResponse<Course> page(String keyword, String department, Boolean isActive, PageRequest pr) {
        List<Course> list = courseMapper.page(keyword, department, isActive, pr.offset(), pr.getPageSize());
        long total = courseMapper.count(keyword, department, isActive);
        return PageResponse.of(total, pr.getPageNo(), pr.getPageSize(), list);
    }

    public List<Course> findByTeacherId(Long teacherId) {
        return courseMapper.findByTeacherId(teacherId);
    }
}


