package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.SchoolSetupRequest;
import com.sillador.strecs.dto.SchoolSetupResponse;
import com.sillador.strecs.entity.SchoolSetup;
import com.sillador.strecs.services.SchoolSetupService;
import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/setup")
@RequiredArgsConstructor
public class SchoolSetupController {

    private final SchoolSetupService schoolSetupService;

    @GetMapping("/status")
    public ResponseEntity<BaseResponse> getSetupStatus() {
        log.info("Checking setup status");
        
        try {
            boolean isComplete = schoolSetupService.isSetupComplete();
            Optional<SchoolSetup> currentSetup = schoolSetupService.getCurrentSetup();
            
            if (isComplete && currentSetup.isPresent()) {
                SchoolSetup setup = currentSetup.get();
                SchoolSetupResponse response = new SchoolSetupResponse(
                        setup.getId(),
                        setup.getSchoolName(),
                        setup.getSystemName(),
                        setup.getAddress(),
                        setup.getContactInformation(),
                        setup.getLogoPath(),
                        setup.getIsSetupComplete(),
                        "Setup is complete"
                );
                return ResponseEntity.ok(new BaseResponse().build(response));
            } else {
                SchoolSetupResponse response = new SchoolSetupResponse();
                response.setIsSetupComplete(false);
                response.setMessage("Setup is required");
                return ResponseEntity.ok(new BaseResponse().build(response));
            }
            
        } catch (Exception e) {
            log.error("Error checking setup status", e);
            return ResponseEntity.badRequest()
                    .body(new BaseResponse().build(ResponseCode.ERROR, "Error checking setup status: " + e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createSetup(@RequestBody SchoolSetupRequest request) {
        log.info("Creating school setup for: {}", request.getSchoolName());
        
        try {
            // Validate required fields
            if (request.getSchoolName() == null || request.getSchoolName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new BaseResponse().build(ResponseCode.ERROR, "School name is required"));
            }
            
            SchoolSetupResponse response = schoolSetupService.createOrUpdateSetup(request);
            return ResponseEntity.ok(new BaseResponse().build(response));
            
        } catch (Exception e) {
            log.error("Error creating school setup", e);
            return ResponseEntity.badRequest()
                    .body(new BaseResponse().build(ResponseCode.ERROR, "Error creating setup: " + e.getMessage()));
        }
    }

    @PostMapping("/complete/{setupId}")
    public ResponseEntity<BaseResponse> completeSetup(@PathVariable Long setupId) {
        log.info("Completing school setup with ID: {}", setupId);
        
        try {
            SchoolSetupResponse response = schoolSetupService.completeSetup(setupId);
            return ResponseEntity.ok(new BaseResponse().build(response));
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid setup completion request", e);
            return ResponseEntity.badRequest()
                    .body(new BaseResponse().build(ResponseCode.ERROR, e.getMessage()));
        } catch (Exception e) {
            log.error("Error completing school setup", e);
            return ResponseEntity.badRequest()
                    .body(new BaseResponse().build(ResponseCode.ERROR, "Error completing setup: " + e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponse> updateSchoolInfo(@RequestBody SchoolSetupRequest request) {
        log.info("Updating school information for: {}", request.getSchoolName());
        
        try {
            // Validate required fields
            if (request.getSchoolName() == null || request.getSchoolName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new BaseResponse().build(ResponseCode.ERROR, "School name is required"));
            }
            
            if (request.getSystemName() == null || request.getSystemName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new BaseResponse().build(ResponseCode.ERROR, "System name is required"));
            }
            
            SchoolSetupResponse response = schoolSetupService.createOrUpdateSetup(request);
            return ResponseEntity.ok(new BaseResponse().build(response));
            
        } catch (Exception e) {
            log.error("Error updating school information", e);
            return ResponseEntity.badRequest()
                    .body(new BaseResponse().build(ResponseCode.ERROR, "Error updating school info: " + e.getMessage()));
        }
    }

    @GetMapping("/current")
    public ResponseEntity<BaseResponse> getCurrentSetup() {
        log.info("Getting current setup information");
        
        try {
            Optional<SchoolSetup> currentSetup = schoolSetupService.getCurrentSetup();
            
            if (currentSetup.isPresent()) {
                SchoolSetup setup = currentSetup.get();
                SchoolSetupResponse response = new SchoolSetupResponse(
                        setup.getId(),
                        setup.getSchoolName(),
                        setup.getSystemName(),
                        setup.getAddress(),
                        setup.getContactInformation(),
                        setup.getLogoPath(),
                        setup.getIsSetupComplete(),
                        "Current setup information"
                );
                return ResponseEntity.ok(new BaseResponse().build(response));
            } else {
                return ResponseEntity.ok(new BaseResponse().build(ResponseCode.ERROR, "No setup found"));
            }
            
        } catch (Exception e) {
            log.error("Error getting current setup", e);
            return ResponseEntity.badRequest()
                    .body(new BaseResponse().build(ResponseCode.ERROR, "Error getting setup: " + e.getMessage()));
        }
    }

    @GetMapping("/system-info")
    public ResponseEntity<BaseResponse> getSystemInfo() {
        log.info("Getting system information for login page");
        
        try {
            Optional<SchoolSetup> currentSetup = schoolSetupService.getCurrentSetup();
            
            if (currentSetup.isPresent()) {
                SchoolSetup setup = currentSetup.get();
                // Return only system name and logo for login page
                var systemInfo = new java.util.HashMap<String, Object>();
                systemInfo.put("systemName", setup.getSystemName() != null ? setup.getSystemName() : "School Management System");
                systemInfo.put("logoPath", setup.getLogoPath());
                systemInfo.put("isSetupComplete", setup.getIsSetupComplete());
                
                return ResponseEntity.ok(new BaseResponse().build(systemInfo));
            } else {
                // Default system info when no setup exists
                var systemInfo = new java.util.HashMap<String, Object>();
                systemInfo.put("systemName", "School Management System");
                systemInfo.put("logoPath", null);
                systemInfo.put("isSetupComplete", false);
                
                return ResponseEntity.ok(new BaseResponse().build(systemInfo));
            }
            
        } catch (Exception e) {
            log.error("Error getting system information", e);
            return ResponseEntity.badRequest()
                    .body(new BaseResponse().build(ResponseCode.ERROR, "Error getting system info: " + e.getMessage()));
        }
    }
}
