package com.sillador.strecs.services.impl;

import com.sillador.strecs.entity.*;
import com.sillador.strecs.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.springframework.stereotype.Service;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptServiceImpl implements TranscriptService {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final AppConfigService appConfigService;

    private static final String TEMPLATE_PATH = "/Users/rssillador/Documents/projects/rssrs/uploads/documents/sample_transcript_template.docx";

    @Override
    public byte[] generateTranscriptPdf(Long studentId, Map<String, Object> templateData) throws IOException {
        log.info("Starting PDF transcript generation for student ID: {}", studentId);
        
        try {
            // Load the student data
            log.info("Loading student data for ID: {}", studentId);
            Optional<Student> studentOpt = studentService.findById(studentId);
            if (studentOpt.isEmpty()) {
                log.error("Student not found with ID: {}", studentId);
                throw new IllegalArgumentException("Student not found with ID: " + studentId);
            }

            Student student = studentOpt.get();
            log.info("Successfully loaded student: {} ({})", student.getFullName(), student.getStudentId());

            // Fetch all required data from database
            Map<String, Object> databaseData = fetchTranscriptDataFromDatabase(student);

            // Load the DOCX template
            log.info("Loading DOCX template from: {}", TEMPLATE_PATH);
            Path templatePath = Paths.get(TEMPLATE_PATH);
            if (!Files.exists(templatePath)) {
                log.error("Template file not found at: {}", TEMPLATE_PATH);
                throw new FileNotFoundException("Template file not found at: " + TEMPLATE_PATH);
            }

            log.info("Template file found, size: {} bytes", Files.size(templatePath));

            try (FileInputStream templateStream = new FileInputStream(templatePath.toFile());
                 XWPFDocument document = new XWPFDocument(templateStream)) {

                log.info("Successfully loaded DOCX template");

                // Replace placeholders in the document
                log.info("Starting placeholder replacement");
                replaceTextInDocument(document, student, databaseData);

                // Convert DOCX to PDF
                log.info("Converting DOCX to PDF");
                ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                
                PdfOptions options = PdfOptions.create();
                PdfConverter.getInstance().convert(document, pdfOutputStream, options);
                
                byte[] result = pdfOutputStream.toByteArray();
                
                log.info("Successfully generated PDF transcript, size: {} bytes", result.length);
                return result;
            }
        } catch (Exception e) {
            log.error("Error generating PDF transcript for student ID {}: {}", studentId, e.getMessage(), e);
            throw new IOException("Error generating PDF transcript: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> fetchTranscriptDataFromDatabase(Student student) {
        log.info("Fetching transcript data from database for student: {}", student.getStudentId());
        
        Map<String, Object> data = new HashMap<>();
        
        try {
            // Get all enrollments for the student ordered by school year
            List<Enrollment> enrollments = enrollmentService.findAllByStudentOrderBySchoolYearAsc(student);
            log.info("Found {} enrollments for student", enrollments.size());
            
            // Build academic records from enrollments and grades
            List<Map<String, Object>> academicRecords = new ArrayList<>();
            
            for (Enrollment enrollment : enrollments) {
                List<Grade> grades = gradeService.findAllByEnrollment(enrollment);
                log.info("Found {} grades for enrollment in year {}", grades.size(), enrollment.getSchoolYear());
                
                // Group grades by subject and grading period
                Map<String, Map<Integer, Float>> subjectGrades = new HashMap<>();
                
                for (Grade grade : grades) {
                    String subjectName = grade.getSubjectCode() != null && grade.getSubjectCode().getSubject() != null 
                        ? grade.getSubjectCode().getSubject().getName() 
                        : "Unknown Subject";
                    
                    subjectGrades.computeIfAbsent(subjectName, k -> new HashMap<>())
                        .put(grade.getGradingPeriod(), grade.getGradeScore());
                }
                
                // Create records for each subject
                for (Map.Entry<String, Map<Integer, Float>> subjectEntry : subjectGrades.entrySet()) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("yearLevel", enrollment.getYearLevel() != null ? enrollment.getYearLevel().getName() : "N/A");
                    record.put("subject", subjectEntry.getKey());
                    
                    Map<Integer, Float> periodGrades = subjectEntry.getValue();
                    record.put("firstQuarter", periodGrades.getOrDefault(1, 0.0f));
                    record.put("secondQuarter", periodGrades.getOrDefault(2, 0.0f));
                    record.put("thirdQuarter", periodGrades.getOrDefault(3, 0.0f));
                    record.put("fourthQuarter", periodGrades.getOrDefault(4, 0.0f));
                    
                    // Calculate final grade (average of quarters)
                    float total = 0.0f;
                    int count = 0;
                    for (Float gradeValue : periodGrades.values()) {
                        if (gradeValue != null && gradeValue > 0) {
                            total += gradeValue;
                            count++;
                        }
                    }
                    float finalGrade = count > 0 ? total / count : 0.0f;
                    record.put("finalGrade", finalGrade);
                    record.put("remarks", finalGrade >= 75 ? "PASSED" : "FAILED");
                    record.put("schoolYear", String.valueOf(enrollment.getSchoolYear()));
                    
                    academicRecords.add(record);
                }
            }
            
            data.put("academicRecords", academicRecords);
            
            // Set default school information
            data.put("schoolName", "Sample High School");
            data.put("schoolAddress", "123 Education Street, Learning City, Knowledge Province 1234");
            data.put("registrarName", "Maria Santos");
            data.put("registrarTitle", "School Registrar");
            
            log.info("Successfully fetched transcript data with {} academic records", academicRecords.size());
            
        } catch (Exception e) {
            log.error("Error fetching transcript data from database: {}", e.getMessage(), e);
            // Set empty academic records if there's an error
            data.put("academicRecords", new ArrayList<>());
            data.put("schoolName", "Sample High School");
            data.put("schoolAddress", "123 Education Street, Learning City, Knowledge Province 1234");
            data.put("registrarName", "Maria Santos");
            data.put("registrarTitle", "School Registrar");
        }
        
        return data;
    }

    private void replaceTextInDocument(XWPFDocument document, Student student, Map<String, Object> databaseData) {
        // Create replacement map
        Map<String, String> replacements = createReplacementMap(student, databaseData);

        // Replace text in paragraphs
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceTextInParagraph(paragraph, replacements);
        }

        // Replace text in tables and handle academic records table
        for (XWPFTable table : document.getTables()) {
            replaceTextInTable(table, replacements);
            replaceAcademicRecordsTable(table, databaseData);
        }

        // Replace text in headers and footers
        document.getHeaderList().forEach(header -> 
            header.getParagraphs().forEach(paragraph -> replaceTextInParagraph(paragraph, replacements))
        );
        
        document.getFooterList().forEach(footer -> 
            footer.getParagraphs().forEach(paragraph -> replaceTextInParagraph(paragraph, replacements))
        );
    }

    private Map<String, String> createReplacementMap(Student student, Map<String, Object> databaseData) {
        Map<String, String> replacements = new HashMap<>();
        
        // Student basic information
        replacements.put("{{STUDENT_ID}}", student.getStudentId() != null ? student.getStudentId() : "");
        replacements.put("{{LRN}}", student.getLrn() != null ? student.getLrn() : "");
        replacements.put("{{FIRST_NAME}}", student.getFirstName() != null ? student.getFirstName() : "");
        replacements.put("{{LAST_NAME}}", student.getLastName() != null ? student.getLastName() : "");
        replacements.put("{{MIDDLE_NAME}}", student.getMiddleName() != null ? student.getMiddleName() : "");
        replacements.put("{{FULL_NAME}}", student.getFullName() != null ? student.getFullName() : "");
        replacements.put("{{GENDER}}", student.getGender() != null ? student.getGender().toString() : "");
        replacements.put("{{EMAIL}}", student.getEmail() != null ? student.getEmail() : "");
        replacements.put("{{ADDRESS}}", student.getAddress() != null ? student.getAddress() : "");
        replacements.put("{{CONTACT_NUMBER}}", student.getContactNumber() != null ? student.getContactNumber() : "");
        
        // Format birthday
        if (student.getBirthday() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            replacements.put("{{BIRTHDAY}}", dateFormat.format(student.getBirthday()));
        } else {
            replacements.put("{{BIRTHDAY}}", "");
        }

        // Current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        replacements.put("{{CURRENT_DATE}}", now.format(dateFormatter));
        replacements.put("{{CURRENT_TIME}}", now.format(timeFormatter));

        // School information from database data
        replacements.put("{{SCHOOL_NAME}}", databaseData.getOrDefault("schoolName", "Sample High School").toString());
        replacements.put("{{SCHOOL_ADDRESS}}", databaseData.getOrDefault("schoolAddress", "123 Education Street, Learning City, Knowledge Province 1234").toString());
        replacements.put("{{REGISTRAR_NAME}}", databaseData.getOrDefault("registrarName", "Maria Santos").toString());
        replacements.put("{{REGISTRAR_TITLE}}", databaseData.getOrDefault("registrarTitle", "School Registrar").toString());

        // Calculate academic summary from database academic records
        if (databaseData.containsKey("academicRecords")) {
            calculateAcademicSummary(replacements, databaseData.get("academicRecords"));
        } else {
            // Default values if no academic records
            replacements.put("{{TOTAL_SUBJECTS}}", "0");
            replacements.put("{{GENERAL_AVERAGE}}", "N/A");
            replacements.put("{{HIGHEST_GRADE}}", "N/A");
            replacements.put("{{LOWEST_GRADE}}", "N/A");
        }

        return replacements;
    }

    @SuppressWarnings("unchecked")
    private void calculateAcademicSummary(Map<String, String> replacements, Object academicRecordsObj) {
        try {
            if (academicRecordsObj instanceof List<?>) {
                List<Map<String, Object>> academicRecords = (List<Map<String, Object>>) academicRecordsObj;
                
                if (academicRecords.isEmpty()) {
                    replacements.put("{{TOTAL_SUBJECTS}}", "0");
                    replacements.put("{{GENERAL_AVERAGE}}", "N/A");
                    replacements.put("{{HIGHEST_GRADE}}", "N/A");
                    replacements.put("{{LOWEST_GRADE}}", "N/A");
                    return;
                }

                int totalSubjects = 0;
                double totalGrades = 0.0;
                double highestGrade = 0.0;
                double lowestGrade = 100.0;
                boolean hasValidGrades = false;

                for (Map<String, Object> record : academicRecords) {
                    Object finalGradeObj = record.get("finalGrade");
                    if (finalGradeObj != null) {
                        try {
                            double finalGrade = 0.0;
                            if (finalGradeObj instanceof Number) {
                                finalGrade = ((Number) finalGradeObj).doubleValue();
                            } else if (finalGradeObj instanceof String) {
                                String gradeStr = finalGradeObj.toString().trim();
                                if (!gradeStr.isEmpty() && !gradeStr.equalsIgnoreCase("N/A")) {
                                    finalGrade = Double.parseDouble(gradeStr);
                                }
                            }

                            if (finalGrade > 0) {
                                totalSubjects++;
                                totalGrades += finalGrade;
                                hasValidGrades = true;
                                
                                if (finalGrade > highestGrade) {
                                    highestGrade = finalGrade;
                                }
                                if (finalGrade < lowestGrade) {
                                    lowestGrade = finalGrade;
                                }
                            }
                        } catch (NumberFormatException e) {
                            // Skip invalid grades
                            log.warn("Invalid grade format in academic record: {}", finalGradeObj);
                        }
                    }
                }

                // Set calculated values
                replacements.put("{{TOTAL_SUBJECTS}}", String.valueOf(totalSubjects));
                
                if (hasValidGrades && totalSubjects > 0) {
                    double generalAverage = totalGrades / totalSubjects;
                    replacements.put("{{GENERAL_AVERAGE}}", String.format("%.2f", generalAverage));
                    replacements.put("{{HIGHEST_GRADE}}", String.format("%.2f", highestGrade));
                    replacements.put("{{LOWEST_GRADE}}", String.format("%.2f", lowestGrade));
                } else {
                    replacements.put("{{GENERAL_AVERAGE}}", "N/A");
                    replacements.put("{{HIGHEST_GRADE}}", "N/A");
                    replacements.put("{{LOWEST_GRADE}}", "N/A");
                }
            } else {
                // Not a valid academic records format
                replacements.put("{{TOTAL_SUBJECTS}}", "0");
                replacements.put("{{GENERAL_AVERAGE}}", "N/A");
                replacements.put("{{HIGHEST_GRADE}}", "N/A");
                replacements.put("{{LOWEST_GRADE}}", "N/A");
            }
        } catch (Exception e) {
            log.error("Error calculating academic summary: {}", e.getMessage());
            replacements.put("{{TOTAL_SUBJECTS}}", "0");
            replacements.put("{{GENERAL_AVERAGE}}", "N/A");
            replacements.put("{{HIGHEST_GRADE}}", "N/A");
            replacements.put("{{LOWEST_GRADE}}", "N/A");
        }
    }

    private void replaceTextInParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {
        String paragraphText = paragraph.getText();
        if (paragraphText != null && !paragraphText.isEmpty()) {
            String replacedText = paragraphText;
            boolean hasReplacements = false;
            
            // Check if any replacements are needed
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                if (replacedText.contains(entry.getKey())) {
                    replacedText = replacedText.replace(entry.getKey(), entry.getValue());
                    hasReplacements = true;
                }
            }
            
            // Only modify if replacements were made
            if (hasReplacements) {
                // Remove all existing runs
                for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }
                
                // Create new run with replaced text
                XWPFRun run = paragraph.createRun();
                run.setText(replacedText);
            }
        }
    }

    private void replaceTextInTable(XWPFTable table, Map<String, String> replacements) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    replaceTextInParagraph(paragraph, replacements);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void replaceAcademicRecordsTable(XWPFTable table, Map<String, Object> databaseData) {
        // Look for the {{ACADEMIC_RECORDS_TABLE}} placeholder in the table
        boolean foundPlaceholder = false;
        XWPFTableRow placeholderRow = null;
        
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                String cellText = cell.getText();
                if (cellText != null && cellText.contains("{{ACADEMIC_RECORDS_TABLE}}")) {
                    foundPlaceholder = true;
                    placeholderRow = row;
                    break;
                }
            }
            if (foundPlaceholder) break;
        }
        
        if (!foundPlaceholder || placeholderRow == null) {
            return; // No placeholder found in this table
        }
        
        log.info("Found {{ACADEMIC_RECORDS_TABLE}} placeholder, replacing with actual data grouped by school year");
        
        // Get academic records from database data
        List<Map<String, Object>> academicRecords = (List<Map<String, Object>>) databaseData.get("academicRecords");
        if (academicRecords == null || academicRecords.isEmpty()) {
            // Replace placeholder with "No academic records found"
            XWPFTableCell cell = placeholderRow.getTableCells().get(0);
            cell.removeParagraph(0); // Remove existing paragraph
            XWPFParagraph paragraph = cell.addParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("No academic records found");
            log.info("No academic records found, added placeholder text");
            return;
        }
        
        // Remove the placeholder row
        int placeholderRowIndex = table.getRows().indexOf(placeholderRow);
        table.removeRow(placeholderRowIndex);
        
        // Group academic records by school year
        Map<String, List<Map<String, Object>>> recordsByYear = new LinkedHashMap<>();
        for (Map<String, Object> record : academicRecords) {
            String schoolYear = record.getOrDefault("schoolYear", "Unknown").toString();
            recordsByYear.computeIfAbsent(schoolYear, k -> new ArrayList<>()).add(record);
        }
        
        // Sort each year's records by subject name
        for (List<Map<String, Object>> yearRecords : recordsByYear.values()) {
            yearRecords.sort((a, b) -> {
                String subjectA = a.getOrDefault("subject", "").toString();
                String subjectB = b.getOrDefault("subject", "").toString();
                return subjectA.compareTo(subjectB);
            });
        }
        
        // Add records grouped by school year
        boolean firstYear = true;
        for (Map.Entry<String, List<Map<String, Object>>> yearEntry : recordsByYear.entrySet()) {
            String schoolYear = yearEntry.getKey();
            List<Map<String, Object>> yearRecords = yearEntry.getValue();
            
            // Add spacing between years (except for the first year)
            if (!firstYear) {
                XWPFTableRow spacerRow = table.createRow();
                while (spacerRow.getTableCells().size() < 8) {
                    spacerRow.createCell();
                }
                // Empty row for spacing
                for (int i = 0; i < 8; i++) {
                    setCellText(spacerRow.getCell(i), "");
                }
            }
            firstYear = false;
            
            // Add school year header row
            XWPFTableRow yearHeaderRow = table.createRow();
            while (yearHeaderRow.getTableCells().size() < 8) {
                yearHeaderRow.createCell();
            }
            
            // Set school year header spanning all columns
            String yearHeader = String.format("SCHOOL YEAR %s-%s", schoolYear, String.valueOf(Integer.parseInt(schoolYear) + 1));
            setCellText(yearHeaderRow.getCell(0), yearHeader);
            
            // Clear other cells (since merge might not work, just make them empty)
            for (int i = 1; i < 8; i++) {
                setCellText(yearHeaderRow.getCell(i), "");
            }
            
            // Make year header bold and centered
            XWPFTableCell yearHeaderCell = yearHeaderRow.getCell(0);
            for (XWPFParagraph paragraph : yearHeaderCell.getParagraphs()) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setBold(true);
                    run.setFontFamily("Times New Roman");
                    run.setFontSize(11);
                }
            }
            
            // Add records for this school year
            for (Map<String, Object> record : yearRecords) {
                XWPFTableRow newRow = table.createRow();
                
                // Ensure we have enough cells
                while (newRow.getTableCells().size() < 8) {
                    newRow.createCell();
                }
                
                // Fill in the data
                setCellText(newRow.getCell(0), record.getOrDefault("yearLevel", "").toString());
                setCellText(newRow.getCell(1), record.getOrDefault("subject", "").toString());
                setCellText(newRow.getCell(2), formatGrade(record.get("firstQuarter")));
                setCellText(newRow.getCell(3), formatGrade(record.get("secondQuarter")));
                setCellText(newRow.getCell(4), formatGrade(record.get("thirdQuarter")));
                setCellText(newRow.getCell(5), formatGrade(record.get("fourthQuarter")));
                setCellText(newRow.getCell(6), formatGrade(record.get("finalGrade")));
                setCellText(newRow.getCell(7), record.getOrDefault("remarks", "").toString());
            }
            
            log.info("Added {} academic records for school year {}", yearRecords.size(), schoolYear);
        }
        
        log.info("Successfully added academic records for {} school years", recordsByYear.size());
    }
    
    private void setCellText(XWPFTableCell cell, String text) {
        // Clear existing content
        for (int i = cell.getParagraphs().size() - 1; i >= 0; i--) {
            cell.removeParagraph(i);
        }
        
        // Add new paragraph with text
        XWPFParagraph paragraph = cell.addParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text != null ? text : "");
        run.setFontFamily("Times New Roman");
        run.setFontSize(10);
    }
    
    private String formatGrade(Object gradeObj) {
        if (gradeObj == null) return "0.00";
        
        if (gradeObj instanceof Number) {
            double grade = ((Number) gradeObj).doubleValue();
            return grade == 0.0 ? "0.00" : String.format("%.2f", grade);
        }
        
        if (gradeObj instanceof String) {
            String gradeStr = gradeObj.toString().trim();
            if (gradeStr.isEmpty()) return "0.00";
            try {
                double grade = Double.parseDouble(gradeStr);
                return grade == 0.0 ? "0.00" : String.format("%.2f", grade);
            } catch (NumberFormatException e) {
                return gradeStr; // Return as-is if not a number
            }
        }
        
        return gradeObj.toString();
    }
}
