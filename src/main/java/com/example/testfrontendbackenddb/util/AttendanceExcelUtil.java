package com.example.testfrontendbackenddb.util;

import com.example.testfrontendbackenddb.entity.HostelAttendance;
import com.example.testfrontendbackenddb.entity.RegisterStudent;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

//public class AttendanceExcelUtil {
//
//    public static byte[] createAttendanceExcel(List<HostelAttendance> records,
//                                               org.springframework.data.jpa.repository.JpaRepository<RegisterStudent, Integer> studentRepo,
//                                               boolean isEdit, HostelAttendance editedRecord) throws Exception {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Attendance");
//        Row header = sheet.createRow(0);
//        String[] headers = {"Name", "Roll No", "Date", "Status", "Edited"};
//        for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
//
//        int rowIdx = 1;
//        CellStyle redStyle = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setColor(IndexedColors.RED.getIndex());
//        redStyle.setFont(font);
//
//        for (HostelAttendance ha : records) {
//            RegisterStudent s = studentRepo.findById(ha.getStudentId()).orElse(null);
//            Row row = sheet.createRow(rowIdx++);
//            row.createCell(0).setCellValue(s != null ? s.getFullName() : "");
//            row.createCell(1).setCellValue(s != null ? s.getEmailId() : "");
//            row.createCell(2).setCellValue(ha.getAttendanceDate().toString());
//            row.createCell(3).setCellValue(ha.getStatus());
//            Cell editedCell = row.createCell(4);
//            editedCell.setCellValue(ha.getEdited() != null && ha.getEdited() ? "Yes" : "No");
//
//            if (ha.getEdited() != null && ha.getEdited() && isEdit && editedRecord != null && ha.getId().equals(editedRecord.getId())) {
//                // Highlight full row red for edit
//                for (int i = 0; i < headers.length; i++) {
//                    row.getCell(i).setCellStyle(redStyle);
//                }
//            }
//        }
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        workbook.write(bos);
//        workbook.close();
//        return bos.toByteArray();
//    }
//
//    public static void exportAndEmailAttendance(List<HostelAttendance> records,
//                                                org.springframework.data.jpa.repository.JpaRepository<RegisterStudent, Integer> studentRepo,
//                                                boolean isEdit, HostelAttendance editedRecord,
//                                                MailService mailService, Date date) {
//        try {
//            byte[] bytes = createAttendanceExcel(records, studentRepo, isEdit, editedRecord);
//            String subject, body;
//            if (isEdit && editedRecord != null) {
//                RegisterStudent s = studentRepo.findById(editedRecord.getStudentId()).orElse(null);
//                subject = "Late Attendance Submission - Edited";
//                body = String.format("Attendance edited for student: %s (%s) on %s.\nThis is a late submission.",
//                        s != null ? s.getFullName() : "", s != null ? s.getEmailId() : "", new Date());
//            } else {
//                subject = "Attendance Submitted - " + date;
//                body = "Attendance has been submitted. Please find attached Excel sheet.";
//            }
//            mailService.sendEmailWithAttachment(subject, body, "attendance.xlsx", bytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}