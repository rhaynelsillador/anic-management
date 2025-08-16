package com.sillador.strecs.services;

import com.sillador.strecs.dto.SchoolSetupRequest;
import com.sillador.strecs.dto.SchoolSetupResponse;
import com.sillador.strecs.entity.SchoolSetup;

import java.util.Optional;

public interface SchoolSetupService {
    
    /**
     * Check if school setup is complete
     */
    boolean isSetupComplete();
    
    /**
     * Get the current school setup information
     */
    Optional<SchoolSetup> getCurrentSetup();
    
    /**
     * Create or update school setup
     */
    SchoolSetupResponse createOrUpdateSetup(SchoolSetupRequest request);
    
    /**
     * Complete the setup and create admin account
     */
    SchoolSetupResponse completeSetup(Long setupId);
}
