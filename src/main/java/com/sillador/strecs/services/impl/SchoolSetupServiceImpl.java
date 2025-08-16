package com.sillador.strecs.services.impl;

import com.sillador.strecs.admin.entity.Account;
import com.sillador.strecs.admin.entity.Role;
import com.sillador.strecs.admin.repository.AccountRepository;
import com.sillador.strecs.admin.repository.RoleRepository;
import com.sillador.strecs.dto.SchoolSetupRequest;
import com.sillador.strecs.dto.SchoolSetupResponse;
import com.sillador.strecs.entity.SchoolSetup;
import com.sillador.strecs.repository.SchoolSetupRepository;
import com.sillador.strecs.services.SchoolSetupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolSetupServiceImpl implements SchoolSetupService {

    private final SchoolSetupRepository schoolSetupRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isSetupComplete() {
        return schoolSetupRepository.existsByIsSetupCompleteTrue();
    }

    @Override
    public Optional<SchoolSetup> getCurrentSetup() {
        return schoolSetupRepository.findCompletedSetup()
                .or(() -> schoolSetupRepository.findLatestSetup());
    }

    @Override
    @Transactional
    public SchoolSetupResponse createOrUpdateSetup(SchoolSetupRequest request) {
        log.info("Creating or updating school setup with name: {}", request.getSchoolName());
        
        // Get existing setup or create new one
        SchoolSetup setup = schoolSetupRepository.findLatestSetup()
                .orElse(new SchoolSetup());
        
        // Update setup information
        setup.setSchoolName(request.getSchoolName());
        setup.setSystemName(request.getSystemName());
        setup.setAddress(request.getAddress());
        setup.setContactInformation(request.getContactInformation());
        setup.setLogoPath(request.getLogoPath());
        
        // Save setup
        setup = schoolSetupRepository.save(setup);
        
        log.info("School setup saved with ID: {}", setup.getId());
        
        return new SchoolSetupResponse(
                setup.getId(),
                setup.getSchoolName(),
                setup.getSystemName(),
                setup.getAddress(),
                setup.getContactInformation(),
                setup.getLogoPath(),
                setup.getIsSetupComplete(),
                "School setup information saved successfully"
        );
    }

    @Override
    @Transactional
    public SchoolSetupResponse completeSetup(Long setupId) {
        log.info("Completing school setup with ID: {}", setupId);
        
        // Find the setup
        SchoolSetup setup = schoolSetupRepository.findById(setupId)
                .orElseThrow(() -> new IllegalArgumentException("School setup not found with ID: " + setupId));
        
        // Validate required fields
        if (setup.getSchoolName() == null || setup.getSchoolName().trim().isEmpty()) {
            throw new IllegalArgumentException("School name is required");
        }
        
        // Create admin account if it doesn't exist
        createAdminAccountIfNotExists();
        
        // Mark setup as complete
        setup.setIsSetupComplete(true);
        setup = schoolSetupRepository.save(setup);
        
        log.info("School setup completed successfully");
        
        return new SchoolSetupResponse(
                setup.getId(),
                setup.getSchoolName(),
                setup.getSystemName(),
                setup.getAddress(),
                setup.getContactInformation(),
                setup.getLogoPath(),
                setup.getIsSetupComplete(),
                "School setup completed successfully. Admin account created."
        );
    }

    private void createAdminAccountIfNotExists() {
        // Check if admin account already exists
        Optional<Account> existingAdmin = accountRepository.findByUsername("admin");
        
        if (existingAdmin.isEmpty()) {
            log.info("Creating admin account");
            
            // Get or create ADMIN role
            Role adminRole = roleRepository.findByName("Admin")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("Admin");
                        newRole.setDescription("Administrator role with full system access");
                        return roleRepository.save(newRole);
                    });
            
            Account adminAccount = new Account();
            adminAccount.setUsername("admin");
            adminAccount.setPassword(passwordEncoder.encode("admin1234"));
            // Email is optional - not setting email allows username-only login
            adminAccount.setFullName("System Administrator");
            adminAccount.setEnabled(true);
            adminAccount.setRoles(Set.of(adminRole));
            
            accountRepository.save(adminAccount);
            log.info("Admin account created successfully");
        } else {
            log.info("Admin account already exists, skipping creation");
        }
    }
}
