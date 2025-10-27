-- 课程时间段关联表
CREATE TABLE course_time_periods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    time_period_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_course_time_period (course_id, time_period_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入示例数据
INSERT INTO course_time_periods (course_id, time_period_id) VALUES
(1, 1), -- 课程1关联时间段1
(2, 1), -- 课程2关联时间段1
(3, 2), -- 课程3关联时间段2
(4, 2); -- 课程4关联时间段2
