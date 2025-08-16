package com.sillador.strecs.rest.controller;

import com.sillador.strecs.services.TranscriptService;
import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/transcript")
@RequiredArgsConstructor
public class TranscriptController {

    private final TranscriptService transcriptService;

    @PostMapping("/generate-pdf")
    public ResponseEntity<BaseResponse> generateTranscriptPdf(@RequestBody Map<String, Object> request) {
        log.info("Received PDF transcript generation request: {}", request);
        
        try {
            // Extract student ID
            if (!request.containsKey("studentId")) {
                log.error("Missing studentId in request");
                return ResponseEntity.badRequest()
                        .body(new BaseResponse().build(ResponseCode.ERROR, "Missing studentId in request"));
            }
            
            Long studentId = Long.valueOf(request.get("studentId").toString());
            log.info("Processing PDF transcript for student ID: {}", studentId);
            
            // Generate PDF transcript using only student ID (all data fetched from database)
            byte[] transcriptData = transcriptService.generateTranscriptPdf(studentId, Map.of());
            
            // Generate unique filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "transcript_" + studentId + "_" + timestamp + ".pdf";
            
            // Save file to uploads directory
            String uploadsDir = "/Users/rssillador/Documents/projects/rssrs/uploads/documents/";
            Path uploadPath = Paths.get(uploadsDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(filename);
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(transcriptData);
                fos.flush();
            }
            
            log.info("Successfully generated and saved PDF transcript: {}, size: {} bytes", filename, transcriptData.length);
            
            // Return JSON response with file path
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("filename", filename);
            responseData.put("filePath", filePath.toString());
            responseData.put("fileSize", transcriptData.length);
            responseData.put("downloadUrl", "/api/v1/files/download/" + filename);
            responseData.put("fileType", "PDF");
            
            return ResponseEntity.ok(new BaseResponse().build(responseData));
                    
        } catch (Exception e) {
            log.error("Failed to generate PDF transcript", e);
            return ResponseEntity.badRequest()
                    .body(new BaseResponse().build(ResponseCode.ERROR, "Failed to generate PDF transcript: " + e.getMessage()));
        }
    }
}
