package com.sillador.strecs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
