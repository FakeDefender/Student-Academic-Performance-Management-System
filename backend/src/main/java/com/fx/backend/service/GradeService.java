package com.fx.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fx.backend.common.cache.Cacheable;
import com.fx.backend.common.exception.BusinessException;
import com.fx.backend.domain.entity.Grade;
import com.fx.backend.domain.entity.GradeWithCourseInfo;
import com.fx.backend.domain.entity.StudentCourseInfo;
import com.fx.backend.mapper.CourseMapper;
import com.fx.backend.mapper.CourseTimePeriodMapper;
import com.fx.backend.mapper.GradeMapper;
import com.fx.backend.mapper.StudentMapper;
import com.fx.backend.mapper.TeacherCourseMapper;
import com.fx.backend.mapper.TeacherMapper;

@Service
public class GradeService {

    private final GradeMapper gradeMapper;
    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final TeacherCourseMapper teacherCourseMapper;
    private final CourseTimePeriodMapper courseTimePeriodMapper;
    private final TimePeriodService timePeriodService;
    private final CacheService cacheService;

    public GradeService(GradeMapper gradeMapper, StudentMapper studentMapper, CourseMapper courseMapper, TeacherMapper teacherMapper, TeacherCourseMapper teacherCourseMapper, CourseTimePeriodMapper courseTimePeriodMapper, TimePeriodService timePeriodService, CacheService cacheService) {
        this.gradeMapper = gradeMapper;
        this.studentMapper = studentMapper;
        this.courseMapper = courseMapper;
        this.teacherMapper = teacherMapper;
        this.teacherCourseMapper = teacherCourseMapper;
        this.courseTimePeriodMapper = courseTimePeriodMapper;
        this.timePeriodService = timePeriodService;
        this.cacheService = cacheService;
    }

    @Transactional
    public Long createOrUpdateGrade(Grade grade) {
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

        // 检查是否存在重复成绩
        Optional<Grade> existingGrade = gradeMapper.findByStudentCourseSemesterYear(
            grade.getStudentId(), grade.getCourseId(), grade.getSemester(), grade.getAcademicYear());
        
        if (existingGrade.isPresent()) {
            // 更新现有成绩
            Grade existing = existingGrade.get();
            existing.setScore(grade.getScore());
            existing.setGradeLetter(grade.getGradeLetter());
            existing.setIsPassed(grade.getIsPassed());
            existing.setExamType(grade.getExamType());
            existing.setRemarks(grade.getRemarks());
            existing.setTeacherId(grade.getTeacherId());
            
            gradeMapper.update(existing);
            
            // 清除相关缓存
            cacheService.clearStudentGradesCache(grade.getStudentId());
            
            return existing.getId();
        } else {
            // 创建新成绩
            gradeMapper.insert(grade);
            
            // 清除相关缓存
            cacheService.clearStudentGradesCache(grade.getStudentId());
            
            return grade.getId();
        }
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
        
        // 清除相关缓存
        cacheService.clearStudentGradesCache(grade.getStudentId());
        
        return grade.getId();
    }

    @Cacheable(keyPrefix = "student_grades_by_id", expireSeconds = 600) // 缓存10分钟
    public List<GradeWithCourseInfo> queryStudentGrades(Long studentId, String semester, String academicYear) {
        // 成绩查询前的开放时间段校验
        timePeriodService.assertOpenNow();
        return gradeMapper.findByStudentWithCourseInfo(studentId, semester, academicYear);
    }

    @Cacheable(keyPrefix = "student_grades_by_number", expireSeconds = 600) // 缓存10分钟
    public List<GradeWithCourseInfo> queryStudentGradesByNumber(String studentNumber, String semester, String academicYear) {
        // 成绩查询前的开放时间段校验
        timePeriodService.assertOpenNow();
        
        // 根据学号查找学生ID
        Long studentId = studentMapper.findByStudentNumber(studentNumber)
            .map(student -> student.getId())
            .orElseThrow(() -> new BusinessException("STUDENT_NOT_FOUND", "学生学号不存在: " + studentNumber));
        
        return gradeMapper.findByStudentWithCourseInfo(studentId, semester, academicYear);
    }

    /**
     * 教师查询自己课程的学生成绩（带权限控制）
     */
    @Cacheable(keyPrefix = "teacher_student_grades", expireSeconds = 600) // 缓存10分钟
    public List<GradeWithCourseInfo> queryStudentGradesByTeacher(Long teacherId, String studentNumber, String semester, String academicYear) {
        // 成绩查询前的开放时间段校验
        timePeriodService.assertOpenNow();
        
        // 根据学号查找学生ID
        Long studentId = studentMapper.findByStudentNumber(studentNumber)
            .map(student -> student.getId())
            .orElseThrow(() -> new BusinessException("STUDENT_NOT_FOUND", "学生学号不存在: " + studentNumber));
        
        // 检查教师是否有权限查看该学生的成绩
        if (!hasTeacherPermissionForStudent(teacherId, studentId)) {
            throw new BusinessException("PERMISSION_DENIED", "您没有权限查看该学生的成绩");
        }
        
        // 获取该学生的成绩列表（包含课程信息）
        List<GradeWithCourseInfo> grades = gradeMapper.findByStudentWithCourseInfo(studentId, semester, academicYear);
        
        // 对每个成绩检查课程级别的时间段控制
        for (GradeWithCourseInfo grade : grades) {
            assertCourseTimePeriodOpen(grade.getCourseId());
        }
        
        return grades;
    }

    /**
     * 获取课程的所有学生
     */
    public List<StudentCourseInfo> getStudentsByCourse(Long courseId) {
        return gradeMapper.findStudentsByCourse(courseId);
    }

    /**
     * 检查教师是否有权限管理指定学生
     */
    private boolean hasTeacherPermissionForStudent(Long teacherId, Long studentId) {
        // 检查该学生是否有该教师教授的课程的成绩
        return teacherCourseMapper.findStudentIdsByTeacherId(teacherId).contains(studentId);
    }

    /**
     * 检查教师是否有权限管理指定课程
     */
    public boolean hasTeacherPermissionForCourse(Long teacherId, Long courseId) {
        return teacherCourseMapper.existsByTeacherAndCourse(teacherId, courseId);
    }

    /**
     * 检查课程是否在开放时间段内
     */
    private void assertCourseTimePeriodOpen(Long courseId) {
        if (!courseTimePeriodMapper.isCourseTimePeriodActive(courseId)) {
            throw new BusinessException("COURSE_TIME_WINDOW_CLOSED", "该课程当前不在成绩查询开放时间段内");
        }
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
        
        // 清除相关缓存
        cacheService.clearStudentGradesCache(grade.getStudentId());
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


