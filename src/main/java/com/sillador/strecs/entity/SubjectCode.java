package com.sillador.strecs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "subject_codes")
public class SubjectCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String code;

    @ManyToOne
    private Subject subject;
    @ManyToOne
    private Room room;
    @ManyToOne
    private Section section;

    @ManyToOne
    private Teacher adviser;

    private int schoolYear;

    private Time startTime;
    private Time endTime;

    private List<String> days;


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
