package com.fx.backend.service;

import com.fx.backend.common.exception.BusinessException;
import com.fx.backend.domain.entity.Grade;
import com.fx.backend.domain.entity.Student;
import com.fx.backend.domain.entity.Course;
import com.fx.backend.domain.entity.Teacher;
import com.fx.backend.mapper.CourseMapper;
import com.fx.backend.mapper.GradeMapper;
import com.fx.backend.mapper.StudentMapper;
import com.fx.backend.mapper.TeacherMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GradeService {

    private final GradeMapper gradeMapper;
    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final TimePeriodService timePeriodService;

    public GradeService(GradeMapper gradeMapper, StudentMapper studentMapper, CourseMapper courseMapper, TeacherMapper teacherMapper, TimePeriodService timePeriodService) {
        this.gradeMapper = gradeMapper;
        this.studentMapper = studentMapper;
        this.courseMapper = courseMapper;
        this.teacherMapper = teacherMapper;
        this.timePeriodService = timePeriodService;
    }

    @Transactional
    public Long createGrade(Grade grade) {
        // 逻辑外键校验
        studentMapper.findById(grade.getStudentId()).orElseThrow(() -> new BusinessException("NOT_FOUND", "学生不存在"));
        courseMapper.findById(grade.getCourseId()).orElseThrow(() -> new BusinessException("NOT_FOUND", "课程不存在"));
        teacherMapper.findById(grade.getTeacherId()).orElseThrow(() -> new BusinessException("NOT_FOUND", "教师不存在"));

        // 分数范围校验
        if (grade.getScore() != null && (grade.getScore() < 0 || grade.getScore() > 100)) {
            throw new BusinessException("INVALID_SCORE", "分数范围应在0-100之间");
        }

        // 根据分数计算等级与通过状态（与数据库触发器一致）
        applyGradeLetter(grade);

        gradeMapper.insert(grade);
        return grade.getId();
    }

    public List<Grade> queryStudentGrades(Long studentId, String semester, String academicYear) {
        // 成绩查询前的开放时间段校验
        timePeriodService.assertOpenNow();
        return gradeMapper.findByStudent(studentId, semester, academicYear);
    }

    @Transactional
    public void updateGrade(Grade grade) {
        if (grade.getId() == null) {
            throw new BusinessException("BAD_REQUEST", "缺少成绩ID");
        }
        if (grade.getScore() != null && (grade.getScore() < 0 || grade.getScore() > 100)) {
            throw new BusinessException("INVALID_SCORE", "分数范围应在0-100之间");
        }
        applyGradeLetter(grade);
        gradeMapper.update(grade);
    }

    private void applyGradeLetter(Grade g) {
        if (g.getScore() == null) return;
        double s = g.getScore();
        if (s >= 90) { g.setGradeLetter("A"); g.setIsPassed(true); }
        else if (s >= 80) { g.setGradeLetter("B"); g.setIsPassed(true); }
        else if (s >= 70) { g.setGradeLetter("C"); g.setIsPassed(true); }
        else if (s >= 60) { g.setGradeLetter("D"); g.setIsPassed(true); }
        else { g.setGradeLetter("F"); g.setIsPassed(false); }
    }
}


