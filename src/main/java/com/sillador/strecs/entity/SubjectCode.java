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
@Table(name = "subject_codes", uniqueConstraints = @UniqueConstraint(columnNames = {"subject_id", "section_id", "school_year"}))
public class SubjectCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String code;

    @ManyToOne
    private Subject subject;
    @ManyToOne
    private Room room;
    @ManyToOne(fetch = FetchType.LAZY)
    private Section section;

    @ManyToOne
    private Teacher adviser;

    private int schoolYear;

    @Column(columnDefinition = "boolean default true")
    private boolean active;

    @Column(columnDefinition = "boolean default false")
    private boolean locked;

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
