package com.fx.backend.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fx.backend.common.exception.BusinessException;
import com.fx.backend.domain.entity.Course;
import com.fx.backend.domain.entity.Grade;
import com.fx.backend.domain.entity.Student;
import com.fx.backend.domain.entity.Teacher;
import com.fx.backend.mapper.CourseMapper;
import com.fx.backend.mapper.GradeMapper;
import com.fx.backend.mapper.StudentMapper;
import com.fx.backend.mapper.TeacherCourseMapper;
import com.fx.backend.mapper.TeacherMapper;

@Service
public class BatchImportService {
    private final GradeMapper gradeMapper;
    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final TeacherCourseMapper teacherCourseMapper;
    private final GradeService gradeService;

    public BatchImportService(GradeMapper gradeMapper, StudentMapper studentMapper, CourseMapper courseMapper, TeacherMapper teacherMapper, TeacherCourseMapper teacherCourseMapper, GradeService gradeService) {
        this.gradeMapper = gradeMapper;
        this.studentMapper = studentMapper;
        this.courseMapper = courseMapper;
        this.teacherMapper = teacherMapper;
        this.teacherCourseMapper = teacherCourseMapper;
        this.gradeService = gradeService;
    }

    @Transactional
    public int importGrades(MultipartFile file, String semester, String academicYear, Long teacherId) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new BusinessException("INVALID_FILE", "文件名不能为空");
        }
        
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        try {
            List<GradeImportData> importDataList;
            if ("csv".equals(fileExtension)) {
                importDataList = parseCsvFile(file);
            } else if ("xlsx".equals(fileExtension) || "xls".equals(fileExtension)) {
                importDataList = parseExcelFile(file);
            } else {
                throw new BusinessException("UNSUPPORTED_FORMAT", "不支持的文件格式，请使用Excel或CSV文件");
            }
            
            int success = 0;
            for (GradeImportData importData : importDataList) {
                try {
                    Grade grade = convertToGrade(importData, semester, academicYear, teacherId);
                    gradeService.createOrUpdateGrade(grade);
                    success++;
                } catch (BusinessException ex) {
                    // 跳过错误行，继续
                }
            }
            return success;
        } catch (Exception e) {
            throw new BusinessException("IMPORT_ERROR", "批量导入失败: " + e.getMessage());
        }
    }
    
    private List<GradeImportData> parseExcelFile(MultipartFile file) throws Exception {
        List<GradeImportData> importDataList = new ArrayList<>();
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        
        // 先判断文件类型
        int fileType = 0; // 0: unknown, 1: xlsx, 2: xls
        try (InputStream in = file.getInputStream()) {
            if (isXlsxFile(fileName, contentType, in)) {
                fileType = 1;
            } else if (isXlsFile(fileName, contentType, in)) {
                fileType = 2;
            } else {
                throw new BusinessException("INVALID_FILE_TYPE", "无法识别的Excel文件类型，请确保文件是.xlsx或.xls格式");
            }
        }
        
        // 重新获取InputStream进行解析
        try (InputStream in = file.getInputStream()) {
            Workbook wb;
            if (fileType == 1) {
                wb = new XSSFWorkbook(in);
            } else {
                wb = new HSSFWorkbook(in);
            }
            
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 跳过表头
                Row r = sheet.getRow(i);
                if (r == null) continue;
                
                // 检查必要的列是否存在
                if (r.getCell(0) == null || r.getCell(1) == null) {
                    continue; // 跳过学生学号或课程代码为空的行
                }
                
                String studentId = r.getCell(0).getStringCellValue().trim();
                String courseCode = r.getCell(1).getStringCellValue().trim();
                Double score = r.getCell(2) != null ? r.getCell(2).getNumericCellValue() : null;
                String examType = r.getCell(3) != null ? r.getCell(3).getStringCellValue().trim() : "FINAL";
                String semester = r.getCell(4) != null ? r.getCell(4).getStringCellValue().trim() : "";
                String academicYear = r.getCell(5) != null ? r.getCell(5).getStringCellValue().trim() : "";
                String teacherId = r.getCell(6) != null ? r.getCell(6).getStringCellValue().trim() : "";
                String remarks = r.getCell(7) != null ? r.getCell(7).getStringCellValue().trim() : "";

                GradeImportData importData = new GradeImportData(
                    studentId, courseCode, score, examType, 
                    semester, academicYear, teacherId, remarks
                );
                importDataList.add(importData);
            }
        }
        return importDataList;
    }
    
    /**
     * 判断是否为.xlsx文件
     */
    private boolean isXlsxFile(String fileName, String contentType, InputStream in) throws Exception {
        if (fileName != null && fileName.endsWith(".xlsx")) {
            return true;
        }
        if (contentType != null && contentType.contains("spreadsheetml")) {
            return true;
        }
        // 检查文件头
        in.mark(10);
        byte[] header = new byte[10];
        in.read(header);
        in.reset();
        // XLSX文件头: 50 4B 03 04 (ZIP文件头)
        return header[0] == 0x50 && header[1] == 0x4B && header[2] == 0x03 && header[3] == 0x04;
    }
    
    /**
     * 判断是否为.xls文件
     */
    private boolean isXlsFile(String fileName, String contentType, InputStream in) throws Exception {
        if (fileName != null && fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")) {
            // 检查文件头以区分旧版Excel和CSV
            in.mark(10);
            byte[] header = new byte[10];
            in.read(header);
            in.reset();
            // XLS文件头: D0 CF 11 E0 A1 B1 1A E1
            return header[0] == (byte)0xD0 && header[1] == (byte)0xCF && 
                   header[2] == 0x11 && header[3] == (byte)0xE0;
        }
        return false;
    }
    
    private List<GradeImportData> parseCsvFile(MultipartFile file) throws Exception {
        List<GradeImportData> importDataList = new ArrayList<>();
        try (InputStream in = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
            
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // 跳过表头
                }
                
                if (line.trim().isEmpty()) {
                    continue; // 跳过空行
                }
                
                String[] columns = line.split(",");
                if (columns.length < 2) {
                    continue; // 跳过列数不足的行
                }
                
                try {
                    String studentId = columns[0].trim();
                    String courseCode = columns[1].trim();
                    Double score = columns.length > 2 && !columns[2].trim().isEmpty() 
                        ? Double.parseDouble(columns[2].trim()) : null;
                    String examType = columns.length > 3 ? columns[3].trim() : "FINAL";
                    String semester = columns.length > 4 ? columns[4].trim() : "";
                    String academicYear = columns.length > 5 ? columns[5].trim() : "";
                    String teacherId = columns.length > 6 ? columns[6].trim() : "";
                    String remarks = columns.length > 7 ? columns[7].trim() : "";

                    GradeImportData importData = new GradeImportData(
                        studentId, courseCode, score, examType, 
                        semester, academicYear, teacherId, remarks
                    );
                    importDataList.add(importData);
                } catch (NumberFormatException e) {
                    // 跳过格式错误的行
                    continue;
                }
            }
        }
        return importDataList;
    }
    
    /**
     * 将导入数据转换为Grade实体
     */
    private Grade convertToGrade(GradeImportData importData, String defaultSemester, String defaultAcademicYear, Long defaultTeacherId) {
        // 根据学号查找学生ID
        Long studentId = findStudentIdByStudentNumber(importData.getStudentId());
        if (studentId == null) {
            throw new BusinessException("STUDENT_NOT_FOUND", "学生学号不存在: " + importData.getStudentId());
        }
        
        // 根据课程代码查找课程ID
        Long courseId = findCourseIdByCourseCode(importData.getCourseCode());
        if (courseId == null) {
            throw new BusinessException("COURSE_NOT_FOUND", "课程代码不存在: " + importData.getCourseCode());
        }
        
        // 根据教师ID查找教师ID（如果提供了的话）
        Long teacherId = defaultTeacherId;
        if (importData.getTeacherId() != null && !importData.getTeacherId().trim().isEmpty()) {
            Long foundTeacherId = findTeacherIdByTeacherNumber(importData.getTeacherId());
            if (foundTeacherId != null) {
                teacherId = foundTeacherId;
            }
        }
        
        // 使用文件中的学期和学年，如果为空则使用默认值
        String semester = importData.getSemester() != null && !importData.getSemester().trim().isEmpty() 
            ? importData.getSemester() : defaultSemester;
        String academicYear = importData.getAcademicYear() != null && !importData.getAcademicYear().trim().isEmpty() 
            ? importData.getAcademicYear() : defaultAcademicYear;
        
        Grade grade = new Grade();
        grade.setStudentId(studentId);
        grade.setCourseId(courseId);
        grade.setTeacherId(teacherId);
        grade.setSemester(semester);
        grade.setAcademicYear(academicYear);
        grade.setScore(importData.getScore());
        grade.setExamType(importData.getExamType() != null ? importData.getExamType() : "FINAL");
        grade.setRemarks(importData.getRemarks());
        
        return grade;
    }
    
    /**
     * 根据学号查找学生ID
     */
    private Long findStudentIdByStudentNumber(String studentNumber) {
        // 这里需要实现根据学号查找学生ID的逻辑
        // 假设StudentMapper有findByStudentId方法
        return studentMapper.findByStudentNumber(studentNumber).map(Student::getId).orElse(null);
    }
    
    /**
     * 根据课程代码查找课程ID
     */
    private Long findCourseIdByCourseCode(String courseCode) {
        // 这里需要实现根据课程代码查找课程ID的逻辑
        // 假设CourseMapper有findByCourseCode方法
        return courseMapper.findByCourseCode(courseCode).map(Course::getId).orElse(null);
    }
    
    /**
     * 根据教师编号查找教师ID
     */
    private Long findTeacherIdByTeacherNumber(String teacherNumber) {
        // 这里需要实现根据教师编号查找教师ID的逻辑
        // 假设TeacherMapper有findByTeacherId方法
        return teacherMapper.findByTeacherNumber(teacherNumber).map(Teacher::getId).orElse(null);
    }

    /**
     * 通过教师工号进行批量导入
     */
    @Transactional
    public int importGradesByTeacherNumber(MultipartFile file, String semester, String academicYear, String teacherNumber) {
        // 根据教师工号查找教师ID
        Long teacherId = findTeacherIdByTeacherNumber(teacherNumber);
        if (teacherId == null) {
            throw new BusinessException("TEACHER_NOT_FOUND", "教师工号不存在: " + teacherNumber);
        }
        
        // 调用原有的导入方法
        return importGrades(file, semester, academicYear, teacherId);
    }

    /**
     * 从文件中解析所有信息进行批量导入
     */
    @Transactional
    public int importGradesFromFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new BusinessException("INVALID_FILE", "文件名不能为空");
        }
        
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        try {
            List<GradeImportData> importDataList;
            if ("csv".equals(fileExtension)) {
                importDataList = parseCsvFile(file);
            } else if ("xlsx".equals(fileExtension) || "xls".equals(fileExtension)) {
                importDataList = parseExcelFile(file);
            } else {
                throw new BusinessException("UNSUPPORTED_FORMAT", "不支持的文件格式，请使用Excel或CSV文件");
            }

            int success = 0;
            for (GradeImportData importData : importDataList) {
                try {
                    Grade grade = convertToGradeFromFile(importData);
                    gradeService.createOrUpdateGrade(grade);
                    success++;
                } catch (BusinessException ex) {
                    // 跳过错误行，继续
                }
            }
            return success;
        } catch (Exception e) {
            throw new BusinessException("IMPORT_ERROR", "批量导入失败: " + e.getMessage());
        }
    }

    /**
     * 从文件数据转换为Grade对象（使用文件中的信息）
     */
    private Grade convertToGradeFromFile(GradeImportData importData) {
        // 根据学号查找学生ID
        Long studentId = findStudentIdByStudentNumber(importData.getStudentId());
        if (studentId == null) {
            throw new BusinessException("STUDENT_NOT_FOUND", "学生学号不存在: " + importData.getStudentId());
        }

        // 根据课程代码查找课程ID
        Long courseId = findCourseIdByCourseCode(importData.getCourseCode());
        if (courseId == null) {
            throw new BusinessException("COURSE_NOT_FOUND", "课程代码不存在: " + importData.getCourseCode());
        }

        // 根据教师工号查找教师ID
        Long teacherId = findTeacherIdByTeacherNumber(importData.getTeacherId());
        if (teacherId == null) {
            throw new BusinessException("TEACHER_NOT_FOUND", "教师工号不存在: " + importData.getTeacherId());
        }

        Grade grade = new Grade();
        grade.setStudentId(studentId);
        grade.setCourseId(courseId);
        grade.setTeacherId(teacherId);
        grade.setSemester(importData.getSemester());
        grade.setAcademicYear(importData.getAcademicYear());
        grade.setScore(importData.getScore());
        grade.setExamType(importData.getExamType() != null ? importData.getExamType() : "FINAL");
        grade.setRemarks(importData.getRemarks());

        return grade;
    }

    /**
     * 教师批量导入成绩（带权限控制）
     */
    @Transactional
    public int importGradesFromFileByTeacher(MultipartFile file, Long teacherId) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new BusinessException("INVALID_FILE", "文件名不能为空");
        }
        
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        try {
            List<GradeImportData> importDataList;
            if ("csv".equals(fileExtension)) {
                importDataList = parseCsvFile(file);
            } else if ("xlsx".equals(fileExtension) || "xls".equals(fileExtension)) {
                importDataList = parseExcelFile(file);
            } else {
                throw new BusinessException("UNSUPPORTED_FORMAT", "不支持的文件格式，请使用Excel或CSV文件");
            }

            int success = 0;
            for (GradeImportData importData : importDataList) {
                try {
                    Grade grade = convertToGradeFromFileWithPermission(importData, teacherId);
                    gradeService.createOrUpdateGrade(grade);
                    success++;
                } catch (BusinessException ex) {
                    // 跳过错误行，继续
                }
            }
            return success;
        } catch (Exception e) {
            throw new BusinessException("IMPORT_ERROR", "批量导入失败: " + e.getMessage());
        }
    }

    /**
     * 从文件数据转换为Grade对象（带权限控制）
     */
    private Grade convertToGradeFromFileWithPermission(GradeImportData importData, Long currentTeacherId) {
        // 根据学号查找学生ID
        Long studentId = findStudentIdByStudentNumber(importData.getStudentId());
        if (studentId == null) {
            throw new BusinessException("STUDENT_NOT_FOUND", "学生学号不存在: " + importData.getStudentId());
        }

        // 根据课程代码查找课程ID
        Long courseId = findCourseIdByCourseCode(importData.getCourseCode());
        if (courseId == null) {
            throw new BusinessException("COURSE_NOT_FOUND", "课程代码不存在: " + importData.getCourseCode());
        }

            // 检查当前教师是否有权限教授该课程
            if (!gradeService.hasTeacherPermissionForCourse(currentTeacherId, courseId)) {
                throw new BusinessException("PERMISSION_DENIED", "您没有权限教授课程: " + importData.getCourseCode());
            }

        Grade grade = new Grade();
        grade.setStudentId(studentId);
        grade.setCourseId(courseId);
        grade.setTeacherId(currentTeacherId); // 使用当前教师ID
        grade.setSemester(importData.getSemester());
        grade.setAcademicYear(importData.getAcademicYear());
        grade.setScore(importData.getScore());
        grade.setExamType(importData.getExamType() != null ? importData.getExamType() : "FINAL");
        grade.setRemarks(importData.getRemarks());

        return grade;
    }
}


