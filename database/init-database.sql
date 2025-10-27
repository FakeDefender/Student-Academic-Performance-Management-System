-- 学生成绩管理系统 - 完整数据库初始化脚本
-- 包含表结构创建和Mock数据插入
-- 用于前后端联调

-- ============================================
-- 1. 创建数据库表结构
-- ============================================

-- Users
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  role ENUM('STUDENT','TEACHER','ADMIN') NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  last_login TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Students
CREATE TABLE IF NOT EXISTS students (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  student_id VARCHAR(20) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  class_name VARCHAR(50),
  major VARCHAR(100),
  enrollment_date DATE,
  graduation_date DATE,
  status ENUM('ACTIVE','GRADUATED','SUSPENDED') DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_students_student_id ON students(student_id);
CREATE INDEX idx_students_user_id ON students(user_id);
CREATE INDEX idx_students_class_name ON students(class_name);
CREATE INDEX idx_students_status ON students(status);

-- Teachers
CREATE TABLE IF NOT EXISTS teachers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  teacher_id VARCHAR(20) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  department VARCHAR(100),
  title VARCHAR(50),
  phone VARCHAR(20),
  office VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_teachers_teacher_id ON teachers(teacher_id);
CREATE INDEX idx_teachers_user_id ON teachers(user_id);
CREATE INDEX idx_teachers_department ON teachers(department);

-- Courses
CREATE TABLE IF NOT EXISTS courses (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  course_code VARCHAR(20) NOT NULL UNIQUE,
  course_name VARCHAR(200) NOT NULL,
  credits INT NOT NULL,
  department VARCHAR(100),
  description TEXT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CHECK (credits > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_courses_course_code ON courses(course_code);
CREATE INDEX idx_courses_department ON courses(department);
CREATE INDEX idx_courses_is_active ON courses(is_active);

-- Grades
CREATE TABLE IF NOT EXISTS grades (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  teacher_id BIGINT NOT NULL,
  semester VARCHAR(20) NOT NULL,
  academic_year VARCHAR(10) NOT NULL,
  score DECIMAL(5,2),
  grade_letter VARCHAR(2),
  is_passed BOOLEAN DEFAULT FALSE,
  exam_type ENUM('MIDTERM','FINAL','ASSIGNMENT','PROJECT') DEFAULT 'FINAL',
  remarks TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_grades (student_id, course_id, semester, academic_year),
  CHECK (score IS NULL OR (score >= 0 AND score <= 100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_grades_student_id ON grades(student_id);
CREATE INDEX idx_grades_course_id ON grades(course_id);
CREATE INDEX idx_grades_teacher_id ON grades(teacher_id);
CREATE INDEX idx_grades_semester ON grades(semester);
CREATE INDEX idx_grades_academic_year ON grades(academic_year);
CREATE INDEX idx_grades_score ON grades(score);

-- Time Periods
CREATE TABLE IF NOT EXISTS time_periods (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  start_date TIMESTAMP NOT NULL,
  end_date TIMESTAMP NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_by BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CHECK (end_date > start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_time_periods_start_date ON time_periods(start_date);
CREATE INDEX idx_time_periods_end_date ON time_periods(end_date);
CREATE INDEX idx_time_periods_is_active ON time_periods(is_active);
CREATE INDEX idx_time_periods_created_by ON time_periods(created_by);

-- Teacher Courses
CREATE TABLE IF NOT EXISTS teacher_courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_teacher_course (teacher_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Course Time Periods
CREATE TABLE IF NOT EXISTS course_time_periods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    time_period_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_course_time_period (course_id, time_period_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- System Configs
CREATE TABLE IF NOT EXISTS system_configs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  config_key VARCHAR(100) NOT NULL UNIQUE,
  config_value TEXT,
  description TEXT,
  config_type ENUM('STRING','NUMBER','BOOLEAN','JSON') DEFAULT 'STRING',
  is_public BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_system_configs_is_public ON system_configs(is_public);

-- ============================================
-- 2. 插入Mock数据
-- ============================================

-- 用户数据
INSERT INTO users (id, username, password, email, role, is_active) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'admin@school.edu', 'ADMIN', TRUE),
(2, 'teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'teacher01@school.edu', 'TEACHER', TRUE),
(3, 'teacher02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'teacher02@school.edu', 'TEACHER', TRUE),
(4, 'teacher03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqkHnK8Y8z8z8z8z8z8z8z8z8', 'teacher03@school.edu', 'TEACHER', TRUE),
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

-- 教师数据
INSERT INTO teachers (id, user_id, teacher_id, name, department, title, phone, office) VALUES
(1, 2, 'T0001', '张教授', '计算机学院', '教授', '13800000001', 'A-301'),
(2, 3, 'T0002', '李老师', '计算机学院', '副教授', '13800000002', 'A-302'),
(3, 4, 'T0003', '王老师', '数学学院', '讲师', '13800000003', 'B-201')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 学生数据
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

-- 课程数据
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

-- 教师课程关联
INSERT INTO teacher_courses (teacher_id, course_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 4), (2, 5), (2, 8),
(3, 6), (3, 7)
ON DUPLICATE KEY UPDATE teacher_id=VALUES(teacher_id);

-- 时间段数据
INSERT INTO time_periods (id, name, description, start_date, end_date, is_active, created_by) VALUES
(1, '2025春季学期成绩查询', '2025年春季学期成绩查询开放时间', '2025-01-01 00:00:00', '2025-12-31 23:59:59', TRUE, 1),
(2, '2025秋季学期成绩查询', '2025年秋季学期成绩查询开放时间', '2025-06-01 00:00:00', '2025-12-31 23:59:59', TRUE, 1),
(3, '2024春季学期成绩查询', '2024年春季学期成绩查询开放时间', '2024-01-01 00:00:00', '2024-12-31 23:59:59', FALSE, 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 课程时间段关联
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

-- 成绩数据
INSERT INTO grades (id, student_id, course_id, teacher_id, semester, academic_year, score, exam_type, remarks) VALUES
(1, 1, 1, 1, '春季', '2024-2025', 85.5, 'FINAL', '平时表现良好'),
(2, 1, 2, 1, '春季', '2024-2025', 92.0, 'FINAL', '期末发挥优异'),
(3, 1, 3, 1, '春季', '2024-2025', 78.5, 'FINAL', '需要加强练习'),
(4, 2, 1, 1, '春季', '2024-2025', 88.0, 'FINAL', '学习认真'),
(5, 2, 2, 1, '春季', '2024-2025', 95.5, 'FINAL', '算法理解深入'),
(6, 2, 3, 1, '春季', '2024-2025', 82.0, 'FINAL', 'SQL掌握较好'),
(7, 3, 1, 1, '春季', '2024-2025', 76.5, 'FINAL', '基础需要巩固'),
(8, 3, 2, 1, '春季', '2024-2025', 89.0, 'FINAL', '进步明显'),
(9, 3, 3, 1, '春季', '2024-2025', 91.5, 'FINAL', '数据库设计优秀'),
(10, 4, 1, 1, '春季', '2024-2025', 93.0, 'FINAL', '编程能力强'),
(11, 4, 2, 1, '春季', '2024-2025', 87.5, 'FINAL', '算法思维清晰'),
(12, 4, 3, 1, '春季', '2024-2025', 85.0, 'FINAL', '数据库应用熟练'),
(13, 5, 1, 1, '春季', '2024-2025', 79.5, 'FINAL', '需要多练习'),
(14, 5, 2, 1, '春季', '2024-2025', 83.0, 'FINAL', '理解能力不错'),
(15, 5, 3, 1, '春季', '2024-2025', 88.5, 'FINAL', 'SQL语句规范'),
(16, 6, 4, 2, '春季', '2024-2025', 90.5, 'FINAL', '网络编程优秀'),
(17, 6, 5, 2, '春季', '2024-2025', 86.0, 'FINAL', '软件工程理解深入'),
(18, 6, 8, 2, '春季', '2024-2025', 94.0, 'FINAL', 'Web开发技能突出'),
(19, 7, 4, 2, '春季', '2024-2025', 84.5, 'FINAL', '学习态度认真'),
(20, 7, 5, 2, '春季', '2024-2025', 91.0, 'FINAL', '项目管理能力强'),
(21, 7, 8, 2, '春季', '2024-2025', 87.5, 'FINAL', '前端技术掌握好'),
(22, 8, 4, 2, '春季', '2024-2025', 88.0, 'FINAL', '网络协议理解好'),
(23, 8, 5, 2, '春季', '2024-2025', 82.5, 'FINAL', '需要加强实践'),
(24, 8, 8, 2, '春季', '2024-2025', 89.5, 'FINAL', '后端开发能力强'),
(25, 9, 6, 3, '春季', '2024-2025', 95.0, 'FINAL', '数学基础扎实'),
(26, 9, 7, 3, '春季', '2024-2025', 92.5, 'FINAL', '线性代数掌握优秀'),
(27, 10, 6, 3, '春季', '2024-2025', 87.0, 'FINAL', '微积分理解较好'),
(28, 10, 7, 3, '春季', '2024-2025', 89.5, 'FINAL', '矩阵运算熟练'),
(29, 1, 1, 1, '秋季', '2024-2025', 91.0, 'FINAL', '秋季学期进步明显'),
(30, 1, 2, 1, '秋季', '2024-2025', 88.5, 'FINAL', '算法设计能力提升'),
(31, 2, 1, 1, '秋季', '2024-2025', 93.5, 'FINAL', 'Java编程熟练'),
(32, 2, 2, 1, '秋季', '2024-2025', 90.0, 'FINAL', '数据结构应用灵活'),
(33, 3, 1, 1, '秋季', '2024-2025', 82.0, 'FINAL', '基础有所改善'),
(34, 3, 2, 1, '秋季', '2024-2025', 85.5, 'FINAL', '算法思维提升'),
(35, 1, 1, 1, '春季', '2025-2026', 89.0, 'FINAL', '新学年开始'),
(36, 1, 2, 1, '春季', '2025-2026', 94.5, 'FINAL', '算法能力持续提升'),
(37, 2, 1, 1, '春季', '2025-2026', 96.0, 'FINAL', '编程技能优秀'),
(38, 2, 2, 1, '春季', '2025-2026', 92.0, 'FINAL', '数据结构掌握深入')
ON DUPLICATE KEY UPDATE score=VALUES(score);

-- 系统配置
INSERT INTO system_configs (id, config_key, config_value, description, config_type, is_public) VALUES
(1, 'system.name', '学生成绩管理系统', '系统名称', 'STRING', TRUE),
(2, 'system.version', '1.0.0', '系统版本', 'STRING', TRUE),
(3, 'grade.passing_score', '60', '及格分数线', 'NUMBER', TRUE),
(4, 'grade.max_score', '100', '最高分数', 'NUMBER', TRUE),
(5, 'file.max_size', '10485760', '文件上传最大大小(字节)', 'NUMBER', FALSE),
(6, 'file.allowed_types', 'xlsx,csv', '允许上传的文件类型', 'STRING', FALSE)
ON DUPLICATE KEY UPDATE config_value=VALUES(config_value);

-- ============================================
-- 3. 数据验证查询
-- ============================================

-- 验证数据插入情况
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
