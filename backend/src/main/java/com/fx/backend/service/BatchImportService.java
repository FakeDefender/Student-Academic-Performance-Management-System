package com.fx.backend.service;

import com.fx.backend.common.exception.BusinessException;
import com.fx.backend.domain.entity.Grade;
import com.fx.backend.mapper.GradeMapper;
import com.fx.backend.mapper.StudentMapper;
import com.fx.backend.mapper.CourseMapper;
import com.fx.backend.mapper.TeacherMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class BatchImportService {
    private final GradeMapper gradeMapper;
    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final GradeService gradeService;

    public BatchImportService(GradeMapper gradeMapper, StudentMapper studentMapper, CourseMapper courseMapper, TeacherMapper teacherMapper, GradeService gradeService) {
        this.gradeMapper = gradeMapper;
        this.studentMapper = studentMapper;
        this.courseMapper = courseMapper;
        this.teacherMapper = teacherMapper;
        this.gradeService = gradeService;
    }

    @Transactional
    public int importGrades(MultipartFile file, String semester, String academicYear, Long teacherId) {
        try (InputStream in = file.getInputStream(); Workbook wb = WorkbookFactory.create(in)) {
            Sheet sheet = wb.getSheetAt(0);
            List<Grade> toInsert = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 跳过表头
                Row r = sheet.getRow(i);
                if (r == null) continue;
                Long studentId = (long) r.getCell(0).getNumericCellValue();
                Long courseId = (long) r.getCell(1).getNumericCellValue();
                Double score = r.getCell(2) != null ? r.getCell(2).getNumericCellValue() : null;

                Grade g = new Grade();
                g.setStudentId(studentId);
                g.setCourseId(courseId);
                g.setTeacherId(teacherId);
                g.setSemester(semester);
                g.setAcademicYear(academicYear);
                g.setScore(score);
                toInsert.add(g);
            }
            int success = 0;
            for (Grade g : toInsert) {
                try {
                    gradeService.createGrade(g);
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
}


