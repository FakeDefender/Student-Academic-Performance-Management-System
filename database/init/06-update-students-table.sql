-- 更新students表，添加个人信息相关字段
ALTER TABLE students 
ADD COLUMN email VARCHAR(100) COMMENT '邮箱',
ADD COLUMN phone VARCHAR(20) COMMENT '电话',
ADD COLUMN address TEXT COMMENT '地址',
ADD COLUMN remarks TEXT COMMENT '备注',
ADD COLUMN grade VARCHAR(20) COMMENT '年级';

-- 更新现有学生数据，设置默认值
UPDATE students SET 
    email = CONCAT('student', student_id, '@example.com'),
    phone = '13800000000',
    grade = '2024级',
    status = 'ACTIVE'
WHERE email IS NULL;
