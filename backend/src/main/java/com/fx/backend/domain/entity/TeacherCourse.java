package com.fx.backend.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourse {
    private Long id;
    private Long teacherId;
    private Long courseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
