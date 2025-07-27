package com.sillador.strecs.entity;

import com.sillador.strecs.enums.GradingPeriod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private Enrollment enrollment;
    @ManyToOne
    private SubjectCode subjectCode;
    @Column(columnDefinition = "DECIMAL(5,2)")
    private float gradeScore;
    private int gradingPeriod;
    @Column(columnDefinition = "TEXT")
    private String remarks;
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;


    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
