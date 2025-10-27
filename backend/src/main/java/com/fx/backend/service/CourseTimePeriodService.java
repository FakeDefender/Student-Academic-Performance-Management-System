package com.fx.backend.service;

import com.fx.backend.common.exception.BusinessException;
import com.fx.backend.domain.entity.CourseTimePeriod;
import com.fx.backend.domain.entity.CourseTimePeriodDetail;
import com.fx.backend.mapper.CourseTimePeriodMapper;
import com.fx.backend.mapper.CourseMapper;
import com.fx.backend.mapper.TimePeriodMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseTimePeriodService {
    private final CourseTimePeriodMapper courseTimePeriodMapper;
    private final CourseMapper courseMapper;
    private final TimePeriodMapper timePeriodMapper;

    public CourseTimePeriodService(CourseTimePeriodMapper courseTimePeriodMapper, CourseMapper courseMapper, TimePeriodMapper timePeriodMapper) {
        this.courseTimePeriodMapper = courseTimePeriodMapper;
        this.courseMapper = courseMapper;
        this.timePeriodMapper = timePeriodMapper;
    }

    @Transactional
    public Long create(CourseTimePeriod courseTimePeriod) {
        // 逻辑外键校验
        courseMapper.findById(courseTimePeriod.getCourseId())
            .orElseThrow(() -> new BusinessException("COURSE_NOT_FOUND", "课程不存在"));
        timePeriodMapper.findById(courseTimePeriod.getTimePeriodId())
            .orElseThrow(() -> new BusinessException("TIME_PERIOD_NOT_FOUND", "时间段不存在"));

        // 检查是否已存在关联
        if (courseTimePeriodMapper.existsByCourseAndTimePeriod(
                courseTimePeriod.getCourseId(), courseTimePeriod.getTimePeriodId())) {
            throw new BusinessException("ALREADY_EXISTS", "该课程与时间段的关联已存在");
        }

        courseTimePeriodMapper.insert(courseTimePeriod);
        return courseTimePeriod.getId();
    }

    @Transactional
    public void delete(Long id) {
        courseTimePeriodMapper.deleteById(id);
    }

    public List<CourseTimePeriod> findByCourseId(Long courseId) {
        return courseTimePeriodMapper.findByCourseId(courseId);
    }

    public List<CourseTimePeriod> findByTimePeriodId(Long timePeriodId) {
        return courseTimePeriodMapper.findByTimePeriodId(timePeriodId);
    }

    public List<CourseTimePeriodDetail> findDetailsByTimePeriodId(Long timePeriodId) {
        return courseTimePeriodMapper.findDetailsByTimePeriodId(timePeriodId);
    }

    public boolean isCourseTimePeriodActive(Long courseId) {
        return courseTimePeriodMapper.isCourseTimePeriodActive(courseId);
    }
}
