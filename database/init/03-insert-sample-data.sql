-- 示例数据（MySQL）
-- 注意：为便于联调，这里显式指定ID；如与现有数据冲突，请先清表或调整ID

-- users
INSERT INTO users (id, username, password, email, role, is_active)
VALUES
  (1, 'admin', 'admin123', 'admin@example.com', 'ADMIN', TRUE)
ON DUPLICATE KEY UPDATE username=VALUES(username);

INSERT INTO users (id, username, password, email, role, is_active)
VALUES
  (2, 'teacher01', 'teacher123', 't01@example.com', 'TEACHER', TRUE)
ON DUPLICATE KEY UPDATE username=VALUES(username);

INSERT INTO users (id, username, password, email, role, is_active)
VALUES
  (3, 'student01', 'student123', 's01@example.com', 'STUDENT', TRUE)
ON DUPLICATE KEY UPDATE username=VALUES(username);

-- teachers
INSERT INTO teachers (id, user_id, teacher_id, name, department, title, phone, office)
VALUES
  (1, 2, 'T0001', '张老师', '计算机学院', '讲师', '13800000001', 'A-301')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- students
INSERT INTO students (id, user_id, student_id, name, class_name, major, status)
VALUES
  (1, 3, 'S0001', '李同学', '计科2201', '计算机科学与技术', 'ACTIVE')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- courses
INSERT INTO courses (id, course_code, course_name, credits, department, description, is_active)
VALUES
  (1, 'CS101', '数据结构', 3, '计算机学院', '基础数据结构与算法', TRUE),
  (2, 'CS102', '数据库系统', 3, '计算机学院', '关系数据库与SQL', TRUE)
ON DUPLICATE KEY UPDATE course_name=VALUES(course_name);

-- grades（为 student01 插入两门课的成绩）
INSERT INTO grades (id, student_id, course_id, teacher_id, semester, academic_year, score, remarks)
VALUES
  (1, 1, 1, 1, '秋季', '2025-2026', 86.5, '平时表现良好'),
  (2, 1, 2, 1, '秋季', '2025-2026', 92.0, '期末发挥优异')
ON DUPLICATE KEY UPDATE score=VALUES(score);

-- time_periods（创建一个当前生效的开放时间段）
INSERT INTO time_periods (id, name, description, start_date, end_date, is_active, created_by)
VALUES
  (1, '成绩查询开放（当前）', '用于联调测试', NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 7 DAY, TRUE, 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);


