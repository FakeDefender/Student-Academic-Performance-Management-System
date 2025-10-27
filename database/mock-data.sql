-- 学生成绩管理系统 - Mock数据
-- 用于前后端联调的最小规模测试数据
-- 执行前请确保已创建所有表结构

-- 清空现有数据（可选，根据需要执行）
-- TRUNCATE TABLE grades;
-- TRUNCATE TABLE teacher_courses;
-- TRUNCATE TABLE course_time_periods;
-- TRUNCATE TABLE time_periods;
-- TRUNCATE TABLE courses;
-- TRUNCATE TABLE students;
-- TRUNCATE TABLE teachers;
-- TRUNCATE TABLE users;

-- ============================================
-- 1. 用户数据 (users)
-- ============================================
INSERT INTO users (id, username, password, email, role, is_active) VALUES
-- 管理员
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'admin@school.edu', 'ADMIN', TRUE),

-- 教师
(2, 'teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'teacher01@school.edu', 'TEACHER', TRUE),
(3, 'teacher02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'teacher02@school.edu', 'TEACHER', TRUE),
(4, 'teacher03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'teacher03@school.edu', 'TEACHER', TRUE),

-- 学生
(5, 'student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'student01@school.edu', 'STUDENT', TRUE),
(6, 'student02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'student02@school.edu', 'STUDENT', TRUE),
(7, 'student03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'student03@school.edu', 'STUDENT', TRUE),
(8, 'student04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'student04@school.edu', 'STUDENT', TRUE),
(9, 'student05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'student05@school.edu', 'STUDENT', TRUE),
(10, 'student06', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'student06@school.edu', 'STUDENT', TRUE),
(11, 'student07', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'student07@school.edu', 'STUDENT', TRUE),
(12, 'student08', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'student08@school.edu', 'STUDENT', TRUE),
(13, 'student09', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'student09@school.edu', 'STUDENT', TRUE),
(14, 'student10', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'student10@school.edu', 'STUDENT', TRUE)

ON DUPLICATE KEY UPDATE username=VALUES(username);

-- ============================================
-- 2. 教师数据 (teachers)
-- ============================================
INSERT INTO teachers (id, user_id, teacher_id, name, department, title, phone, office) VALUES
(1, 2, 'T0001', '张教授', '计算机学院', '教授', '13800000001', 'A-301'),
(2, 3, 'T0002', '李老师', '计算机学院', '副教授', '13800000002', 'A-302'),
(3, 4, 'T0003', '王老师', '数学学院', '讲师', '13800000003', 'B-201')

ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ============================================
-- 3. 学生数据 (students)
-- ============================================
INSERT INTO students (id, user_id, student_id, name, class_name, major, enrollment_date, status) VALUES
(1, 5, 'S0001', '张三', '计科2201', '计算机科学与技术', '2022-09-01', 'ACTIVE'),
(2, 6, 'S0002', '李四', '计科2201', '计算机科学与技术', '2022-09-01', 'ACTIVE'),
(3, 7, 'S0003', '王五', '计科2201', '计算机科学与技术', '2022-09-01', 'ACTIVE'),
(4, 8, 'S0004', '赵六', '计科2202', '计算机科学与技术', '2022-09-01', 'ACTIVE'),
(5, 9, 'S0005', '钱七', '计科2202', '计算机科学与技术', '2022-09-01', 'ACTIVE'),
(6, 10, 'S0006', '孙八', '软工2201', '软件工程', '2022-09-01', 'ACTIVE'),
(7, 11, 'S0007', '周九', '软工2201', '软件工程', '2022-09-01', 'ACTIVE'),
(8, 12, 'S0008', '吴十', '软工2201', '软件工程', '2022-09-01', 'ACTIVE'),
(9, 13, 'S0009', '郑十一', '数学2201', '数学与应用数学', '2022-09-01', 'ACTIVE'),
(10, 14, 'S0010', '王十二', '数学2201', '数学与应用数学', '2022-09-01', 'ACTIVE')

ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ============================================
-- 4. 课程数据 (courses)
-- ============================================
INSERT INTO courses (id, course_code, course_name, credits, department, description, is_active) VALUES
(1, 'CS101', 'Java程序设计', 3, '计算机学院', 'Java基础语法和面向对象编程', TRUE),
(2, 'CS102', '数据结构与算法', 4, '计算机学院', '基础数据结构与算法设计', TRUE),
(3, 'CS103', '数据库系统原理', 3, '计算机学院', '关系数据库设计与SQL语言', TRUE),
(4, 'CS104', '计算机网络', 3, '计算机学院', '网络协议与网络编程', TRUE),
(5, 'CS105', '软件工程', 3, '计算机学院', '软件开发过程与方法', TRUE),
(6, 'MATH101', '高等数学', 4, '数学学院', '微积分基础', TRUE),
(7, 'MATH102', '线性代数', 3, '数学学院', '矩阵与线性变换', TRUE),
(8, 'CS106', 'Web开发技术', 3, '计算机学院', '前端与后端Web开发', TRUE),
(9, 'CS107', '操作系统', 3, '计算机学院', '操作系统原理与设计', TRUE),
(10, 'CS108', '编译原理', 3, '计算机学院', '编译器设计与实现', TRUE)

ON DUPLICATE KEY UPDATE course_name=VALUES(course_name);

-- ============================================
-- 5. 教师课程关联 (teacher_courses)
-- ============================================
INSERT INTO teacher_courses (teacher_id, course_id) VALUES
-- 张教授教授的课程
(1, 1), (1, 2), (1, 3),
-- 李老师教授的课程
(2, 4), (2, 5), (2, 8),
-- 王老师教授的课程
(3, 6), (3, 7)

ON DUPLICATE KEY UPDATE teacher_id=VALUES(teacher_id);

-- ============================================
-- 6. 时间段数据 (time_periods)
-- ============================================
INSERT INTO time_periods (id, name, description, start_date, end_date, is_active, created_by) VALUES
(1, '2025春季学期成绩查询', '2025年春季学期成绩查询开放时间', '2025-01-01 00:00:00', '2025-12-31 23:59:59', TRUE, 1),
(2, '2025秋季学期成绩查询', '2025年秋季学期成绩查询开放时间', '2025-06-01 00:00:00', '2025-12-31 23:59:59', TRUE, 1),
(3, '2024春季学期成绩查询', '2024年春季学期成绩查询开放时间', '2024-01-01 00:00:00', '2024-12-31 23:59:59', FALSE, 1)

ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ============================================
-- 7. 课程时间段关联 (course_time_periods)
-- ============================================
INSERT INTO course_time_periods (course_id, time_period_id) VALUES
(1, 1), (1, 2),
(2, 1), (2, 2),
(3, 1), (3, 2),
(4, 1), (4, 2),
(5, 1), (5, 2),
(6, 1), (6, 2),
(7, 1), (7, 2),
(8, 1), (8, 2),
(9, 1), (9, 2),
(10, 1), (10, 2)

ON DUPLICATE KEY UPDATE course_id=VALUES(course_id);

-- ============================================
-- 8. 成绩数据 (grades)
-- ============================================
INSERT INTO grades (id, student_id, course_id, teacher_id, semester, academic_year, score, exam_type, remarks) VALUES
-- 张三的成绩
(1, 1, 1, 1, '春季', '2024-2025', 85.5, 'FINAL', '平时表现良好'),
(2, 1, 2, 1, '春季', '2024-2025', 92.0, 'FINAL', '期末发挥优异'),
(3, 1, 3, 1, '春季', '2024-2025', 78.5, 'FINAL', '需要加强练习'),

-- 李四的成绩
(4, 2, 1, 1, '春季', '2024-2025', 88.0, 'FINAL', '学习认真'),
(5, 2, 2, 1, '春季', '2024-2025', 95.5, 'FINAL', '算法理解深入'),
(6, 2, 3, 1, '春季', '2024-2025', 82.0, 'FINAL', 'SQL掌握较好'),

-- 王五的成绩
(7, 3, 1, 1, '春季', '2024-2025', 76.5, 'FINAL', '基础需要巩固'),
(8, 3, 2, 1, '春季', '2024-2025', 89.0, 'FINAL', '进步明显'),
(9, 3, 3, 1, '春季', '2024-2025', 91.5, 'FINAL', '数据库设计优秀'),

-- 赵六的成绩
(10, 4, 1, 1, '春季', '2024-2025', 93.0, 'FINAL', '编程能力强'),
(11, 4, 2, 1, '春季', '2024-2025', 87.5, 'FINAL', '算法思维清晰'),
(12, 4, 3, 1, '春季', '2024-2025', 85.0, 'FINAL', '数据库应用熟练'),

-- 钱七的成绩
(13, 5, 1, 1, '春季', '2024-2025', 79.5, 'FINAL', '需要多练习'),
(14, 5, 2, 1, '春季', '2024-2025', 83.0, 'FINAL', '理解能力不错'),
(15, 5, 3, 1, '春季', '2024-2025', 88.5, 'FINAL', 'SQL语句规范'),

-- 孙八的成绩（软工专业）
(16, 6, 4, 2, '春季', '2024-2025', 90.5, 'FINAL', '网络编程优秀'),
(17, 6, 5, 2, '春季', '2024-2025', 86.0, 'FINAL', '软件工程理解深入'),
(18, 6, 8, 2, '春季', '2024-2025', 94.0, 'FINAL', 'Web开发技能突出'),

-- 周九的成绩
(19, 7, 4, 2, '春季', '2024-2025', 84.5, 'FINAL', '学习态度认真'),
(20, 7, 5, 2, '春季', '2024-2025', 91.0, 'FINAL', '项目管理能力强'),
(21, 7, 8, 2, '春季', '2024-2025', 87.5, 'FINAL', '前端技术掌握好'),

-- 吴十的成绩
(22, 8, 4, 2, '春季', '2024-2025', 88.0, 'FINAL', '网络协议理解好'),
(23, 8, 5, 2, '春季', '2024-2025', 82.5, 'FINAL', '需要加强实践'),
(24, 8, 8, 2, '春季', '2024-2025', 89.5, 'FINAL', '后端开发能力强'),

-- 郑十一的成绩（数学专业）
(25, 9, 6, 3, '春季', '2024-2025', 95.0, 'FINAL', '数学基础扎实'),
(26, 9, 7, 3, '春季', '2024-2025', 92.5, 'FINAL', '线性代数掌握优秀'),

-- 王十二的成绩
(27, 10, 6, 3, '春季', '2024-2025', 87.0, 'FINAL', '微积分理解较好'),
(28, 10, 7, 3, '春季', '2024-2025', 89.5, 'FINAL', '矩阵运算熟练'),

-- 秋季学期成绩（部分学生）
(29, 1, 1, 1, '秋季', '2024-2025', 91.0, 'FINAL', '秋季学期进步明显'),
(30, 1, 2, 1, '秋季', '2024-2025', 88.5, 'FINAL', '算法设计能力提升'),
(31, 2, 1, 1, '秋季', '2024-2025', 93.5, 'FINAL', 'Java编程熟练'),
(32, 2, 2, 1, '秋季', '2024-2025', 90.0, 'FINAL', '数据结构应用灵活'),
(33, 3, 1, 1, '秋季', '2024-2025', 82.0, 'FINAL', '基础有所改善'),
(34, 3, 2, 1, '秋季', '2024-2025', 85.5, 'FINAL', '算法思维提升'),

-- 2025-2026学年成绩
(35, 1, 1, 1, '春季', '2025-2026', 89.0, 'FINAL', '新学年开始'),
(36, 1, 2, 1, '春季', '2025-2026', 94.5, 'FINAL', '算法能力持续提升'),
(37, 2, 1, 1, '春季', '2025-2026', 96.0, 'FINAL', '编程技能优秀'),
(38, 2, 2, 1, '春季', '2025-2026', 92.0, 'FINAL', '数据结构掌握深入')

ON DUPLICATE KEY UPDATE score=VALUES(score);

-- ============================================
-- 9. 系统配置 (system_configs)
-- ============================================
INSERT INTO system_configs (id, config_key, config_value, description, config_type, is_public) VALUES
(1, 'system.name', '学生成绩管理系统', '系统名称', 'STRING', TRUE),
(2, 'system.version', '1.0.0', '系统版本', 'STRING', TRUE),
(3, 'grade.passing_score', '60', '及格分数线', 'NUMBER', TRUE),
(4, 'grade.max_score', '100', '最高分数', 'NUMBER', TRUE),
(5, 'file.max_size', '10485760', '文件上传最大大小(字节)', 'NUMBER', FALSE),
(6, 'file.allowed_types', 'xlsx,csv', '允许上传的文件类型', 'STRING', FALSE)

ON DUPLICATE KEY UPDATE config_value=VALUES(config_value);

-- ============================================
-- 查询验证数据
-- ============================================
-- 验证用户数据
SELECT 'Users' as table_name, COUNT(*) as count FROM users;
SELECT 'Teachers' as table_name, COUNT(*) as count FROM teachers;
SELECT 'Students' as table_name, COUNT(*) as count FROM students;
SELECT 'Courses' as table_name, COUNT(*) as count FROM courses;
SELECT 'Teacher Courses' as table_name, COUNT(*) as count FROM teacher_courses;
SELECT 'Grades' as table_name, COUNT(*) as count FROM grades;
SELECT 'Time Periods' as table_name, COUNT(*) as count FROM time_periods;
SELECT 'Course Time Periods' as table_name, COUNT(*) as count FROM course_time_periods;

-- 验证教师课程关联
SELECT t.name as teacher_name, c.course_name, c.course_code 
FROM teachers t 
JOIN teacher_courses tc ON t.id = tc.teacher_id 
JOIN courses c ON tc.course_id = c.id 
ORDER BY t.name, c.course_name;

-- 验证学生成绩
SELECT s.name as student_name, s.student_id, c.course_name, g.score, g.grade_letter, g.semester, g.academic_year
FROM students s 
JOIN grades g ON s.id = g.student_id 
JOIN courses c ON g.course_id = c.id 
ORDER BY s.name, c.course_name, g.academic_year, g.semester;

-- 验证时间段配置
SELECT tp.name, tp.start_date, tp.end_date, tp.is_active, COUNT(ctp.course_id) as course_count
FROM time_periods tp 
LEFT JOIN course_time_periods ctp ON tp.id = ctp.time_period_id 
GROUP BY tp.id 
ORDER BY tp.start_date;
