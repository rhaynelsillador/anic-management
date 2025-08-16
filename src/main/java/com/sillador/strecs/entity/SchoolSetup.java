package com.sillador.strecs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "school_setup")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolSetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    @Column(name = "system_name", nullable = false)
    private String systemName;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "contact_information", columnDefinition = "TEXT")
    private String contactInformation;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "is_setup_complete", nullable = false)
    private Boolean isSetupComplete = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
