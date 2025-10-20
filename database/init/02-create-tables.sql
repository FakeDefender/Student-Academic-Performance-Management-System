-- MySQL 8.0 schema for Student Academic Performance Management System
-- NOTE: Logical foreign keys only (no physical FK constraints)

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

-- Time Periods (query open windows)
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

-- File uploads (import logs)
CREATE TABLE IF NOT EXISTS file_uploads (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  filename VARCHAR(255) NOT NULL,
  original_filename VARCHAR(255) NOT NULL,
  file_path VARCHAR(500) NOT NULL,
  file_size BIGINT NOT NULL,
  file_type VARCHAR(50) NOT NULL,
  upload_type ENUM('GRADES','STUDENTS','COURSES') NOT NULL,
  status ENUM('PENDING','PROCESSING','SUCCESS','FAILED') DEFAULT 'PENDING',
  total_records INT DEFAULT 0,
  success_records INT DEFAULT 0,
  failed_records INT DEFAULT 0,
  error_message TEXT,
  uploaded_by BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_file_uploads_uploaded_by ON file_uploads(uploaded_by);
CREATE INDEX idx_file_uploads_status ON file_uploads(status);
CREATE INDEX idx_file_uploads_upload_type ON file_uploads(upload_type);
CREATE INDEX idx_file_uploads_created_at ON file_uploads(created_at);

-- System configs
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

-- Operation logs
CREATE TABLE IF NOT EXISTS operation_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  operation_type VARCHAR(50) NOT NULL,
  operation_description TEXT,
  target_table VARCHAR(50),
  target_id BIGINT,
  old_values JSON,
  new_values JSON,
  ip_address VARCHAR(45),
  user_agent TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_operation_logs_user_id ON operation_logs(user_id);
CREATE INDEX idx_operation_logs_operation_type ON operation_logs(operation_type);
CREATE INDEX idx_operation_logs_target_table ON operation_logs(target_table);
CREATE INDEX idx_operation_logs_created_at ON operation_logs(created_at);

-- Trigger: auto compute grade_letter/is_passed based on score (optional if handled in service)
DELIMITER $$
CREATE TRIGGER trg_grades_before_insert
BEFORE INSERT ON grades
FOR EACH ROW
BEGIN
  IF NEW.score IS NOT NULL THEN
    IF NEW.score >= 90 THEN SET NEW.grade_letter='A', NEW.is_passed=TRUE;
    ELSEIF NEW.score >= 80 THEN SET NEW.grade_letter='B', NEW.is_passed=TRUE;
    ELSEIF NEW.score >= 70 THEN SET NEW.grade_letter='C', NEW.is_passed=TRUE;
    ELSEIF NEW.score >= 60 THEN SET NEW.grade_letter='D', NEW.is_passed=TRUE;
    ELSE SET NEW.grade_letter='F', NEW.is_passed=FALSE; END IF;
  END IF;
END$$

CREATE TRIGGER trg_grades_before_update
BEFORE UPDATE ON grades
FOR EACH ROW
BEGIN
  IF NEW.score IS NOT NULL THEN
    IF NEW.score >= 90 THEN SET NEW.grade_letter='A', NEW.is_passed=TRUE;
    ELSEIF NEW.score >= 80 THEN SET NEW.grade_letter='B', NEW.is_passed=TRUE;
    ELSEIF NEW.score >= 70 THEN SET NEW.grade_letter='C', NEW.is_passed=TRUE;
    ELSEIF NEW.score >= 60 THEN SET NEW.grade_letter='D', NEW.is_passed=TRUE;
    ELSE SET NEW.grade_letter='F', NEW.is_passed=FALSE; END IF;
  END IF;
END$$
DELIMITER ;


