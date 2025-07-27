package com.sillador.strecs.entity;

import com.sillador.strecs.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private Student student;
    @ManyToOne
    private SubjectCode subjectCode;
    @ManyToOne
    private Section section;
    private java.sql.Date date;
    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;
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
