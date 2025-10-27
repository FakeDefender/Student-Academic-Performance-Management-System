-- 学生成绩管理系统 - 快速联调测试数据
-- 最小数据集，用于前后端联调测试

-- ============================================
-- 快速测试数据插入
-- ============================================

-- 1. 用户数据 (3个用户：1个管理员，1个教师，1个学生)
INSERT INTO users (id, username, password, email, role, is_active) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'admin@school.edu', 'ADMIN', TRUE),
(2, 'teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'teacher01@school.edu', 'TEACHER', TRUE),
(3, 'student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'student01@school.edu', 'STUDENT', TRUE)
ON DUPLICATE KEY UPDATE username=VALUES(username);

-- 2. 教师数据
INSERT INTO teachers (id, user_id, teacher_id, name, department, title, phone, office) VALUES
(1, 2, 'T0001', '张老师', '计算机学院', '讲师', '13800000001', 'A-301')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 3. 学生数据
INSERT INTO students (id, user_id, student_id, name, class_name, major, enrollment_date, status) VALUES
(1, 3, 'S0001', '张三', '计科2201', '计算机科学与技术', '2022-09-01', 'ACTIVE')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 4. 课程数据 (3门课程)
INSERT INTO courses (id, course_code, course_name, credits, department, description, is_active) VALUES
(1, 'CS101', 'Java程序设计', 3, '计算机学院', 'Java基础语法和面向对象编程', TRUE),
(2, 'CS102', '数据结构与算法', 4, '计算机学院', '基础数据结构与算法设计', TRUE),
(3, 'CS103', '数据库系统原理', 3, '计算机学院', '关系数据库设计与SQL语言', TRUE)
ON DUPLICATE KEY UPDATE course_name=VALUES(course_name);

-- 5. 教师课程关联
INSERT INTO teacher_courses (teacher_id, course_id) VALUES
(1, 1), (1, 2), (1, 3)
ON DUPLICATE KEY UPDATE teacher_id=VALUES(teacher_id);

-- 6. 时间段数据
INSERT INTO time_periods (id, name, description, start_date, end_date, is_active, created_by) VALUES
(1, '2025春季学期成绩查询', '2025年春季学期成绩查询开放时间', '2025-01-01 00:00:00', '2025-12-31 23:59:59', TRUE, 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 7. 课程时间段关联
INSERT INTO course_time_periods (course_id, time_period_id) VALUES
(1, 1), (2, 1), (3, 1)
ON DUPLICATE KEY UPDATE course_id=VALUES(course_id);

-- 8. 成绩数据 (学生张三的3门课程成绩)
INSERT INTO grades (id, student_id, course_id, teacher_id, semester, academic_year, score, exam_type, remarks) VALUES
(1, 1, 1, 1, '春季', '2024-2025', 85.5, 'FINAL', '平时表现良好'),
(2, 1, 2, 1, '春季', '2024-2025', 92.0, 'FINAL', '期末发挥优异'),
(3, 1, 3, 1, '春季', '2024-2025', 78.5, 'FINAL', '需要加强练习'),
(4, 1, 1, 1, '秋季', '2024-2025', 91.0, 'FINAL', '秋季学期进步明显'),
(5, 1, 2, 1, '秋季', '2024-2025', 88.5, 'FINAL', '算法设计能力提升')
ON DUPLICATE KEY UPDATE score=VALUES(score);

-- ============================================
-- 测试账号信息
-- ============================================
-- 管理员账号: admin / admin123
-- 教师账号: teacher01 / teacher123  
-- 学生账号: student01 / student123

-- ============================================
-- 验证数据
-- ============================================
SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Teachers', COUNT(*) FROM teachers
UNION ALL
SELECT 'Students', COUNT(*) FROM students
UNION ALL
SELECT 'Courses', COUNT(*) FROM courses
UNION ALL
SELECT 'Teacher Courses', COUNT(*) FROM teacher_courses
UNION ALL
SELECT 'Grades', COUNT(*) FROM grades
UNION ALL
SELECT 'Time Periods', COUNT(*) FROM time_periods
UNION ALL
SELECT 'Course Time Periods', COUNT(*) FROM course_time_periods;

-- 查看学生成绩
SELECT s.name as student_name, s.student_id, c.course_name, g.score, g.grade_letter, g.semester, g.academic_year
FROM students s 
JOIN grades g ON s.id = g.student_id 
JOIN courses c ON g.course_id = c.id 
ORDER BY g.academic_year, g.semester, c.course_name;

-- 查看教师课程
SELECT t.name as teacher_name, c.course_name, c.course_code 
FROM teachers t 
JOIN teacher_courses tc ON t.id = tc.teacher_id 
JOIN courses c ON tc.course_id = c.id 
ORDER BY t.name, c.course_name;
