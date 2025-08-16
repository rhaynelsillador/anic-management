package com.sillador.strecs.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private static final String UPLOADS_DIR = "/Users/rssillador/Documents/projects/rssrs/uploads/documents/";

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            log.info("Request to download file: {}", filename);
            
            // Security: prevent path traversal attacks
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                log.warn("Invalid filename detected: {}", filename);
                return ResponseEntity.badRequest().build();
            }
            
            Path filePath = Paths.get(UPLOADS_DIR, filename);
            
            if (!Files.exists(filePath)) {
                log.warn("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            if (!Files.isReadable(filePath)) {
                log.warn("File not readable: {}", filePath);
                return ResponseEntity.status(403).build();
            }
            
            Resource resource = new FileSystemResource(filePath);
            
            // Determine content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                // Default to DOCX for transcript files
                if (filename.endsWith(".docx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                } else {
                    contentType = "application/octet-stream";
                }
            }
            
            log.info("Serving file: {} ({}), content-type: {}", filename, Files.size(filePath), contentType);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("Error serving file: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
