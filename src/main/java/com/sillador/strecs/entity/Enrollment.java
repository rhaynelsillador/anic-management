package com.sillador.strecs.entity;

import com.sillador.strecs.enums.EnrollmentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "enrollments")
public class Enrollment {

    public Enrollment(){

    }
    public Enrollment(Student student, Section section){
        this.section = section;
        this.student = student;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private Section section;
    @ManyToOne
    private YearLevel yearLevel;
    @ManyToOne
    private Student student;
    private int schoolYear;
    private Date enrollmentDate;

    @Enumerated(EnumType.ORDINAL)
    private EnrollmentType enrollmentType = EnrollmentType.NEW;

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
