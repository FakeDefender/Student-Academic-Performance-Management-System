-- 教师课程关联表
CREATE TABLE teacher_courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_teacher_course (teacher_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入示例数据
INSERT INTO teacher_courses (teacher_id, course_id) VALUES
(1, 1), -- 教师1教授课程1
(1, 2), -- 教师1教授课程2
(2, 3), -- 教师2教授课程3
(2, 4); -- 教师2教授课程4
