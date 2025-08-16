package com.sillador.strecs.repository;

import com.sillador.strecs.entity.SchoolSetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolSetupRepository extends JpaRepository<SchoolSetup, Long> {
    
    @Query("SELECT s FROM SchoolSetup s WHERE s.isSetupComplete = true ORDER BY s.createdAt DESC LIMIT 1")
    Optional<SchoolSetup> findCompletedSetup();
    
    @Query("SELECT s FROM SchoolSetup s ORDER BY s.createdAt DESC LIMIT 1")
    Optional<SchoolSetup> findLatestSetup();
    
    boolean existsByIsSetupCompleteTrue();
}
