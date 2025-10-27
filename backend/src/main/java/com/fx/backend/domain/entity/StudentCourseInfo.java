package com.fx.backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseInfo {
    private Long studentId;
    private String studentNumber;
    private String studentName;
    private String className;
    private String major;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Double latestScore;
    private String latestGradeLetter;
    private Boolean latestIsPassed;
    private String latestSemester;
    private String latestAcademicYear;
}
