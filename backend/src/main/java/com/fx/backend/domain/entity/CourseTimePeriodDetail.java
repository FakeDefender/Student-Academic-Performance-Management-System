package com.fx.backend.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseTimePeriodDetail {
    private Long id;
    private Long courseId;
    private Long timePeriodId;
    private String courseCode;
    private String courseName;
    private String timePeriodName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
