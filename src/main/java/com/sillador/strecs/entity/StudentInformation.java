package com.sillador.strecs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Table
@Entity
public class StudentInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date graduated;
    private Integer batch;
    private String motherName;
    private String fatherName;
    private String parentContactInfo;
    private String parentAddress;
    private String guardian;
    private String guardianContactInfo;
    private String guardianAddress;

    private String religion;

    private String nationality;


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
