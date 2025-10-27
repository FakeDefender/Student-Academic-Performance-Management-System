package com.fx.backend.service;

/**
 * 成绩导入数据类
 * 用于解析Excel/CSV文件中的成绩数据
 */
public class GradeImportData {
    private String studentId;      // 学生学号
    private String courseCode;     // 课程代码
    private Double score;          // 分数
    private String examType;       // 考试类型
    private String semester;       // 学期
    private String academicYear;   // 学年
    private String teacherId;      // 教师ID
    private String remarks;        // 备注

    // 构造函数
    public GradeImportData() {}

    public GradeImportData(String studentId, String courseCode, Double score, String examType, 
                          String semester, String academicYear, String teacherId, String remarks) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.score = score;
        this.examType = examType;
        this.semester = semester;
        this.academicYear = academicYear;
        this.teacherId = teacherId;
        this.remarks = remarks;
    }

    // Getter和Setter方法
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
