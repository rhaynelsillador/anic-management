package com.sillador.strecs.entity;

import com.sillador.strecs.enums.Gender;
import com.sillador.strecs.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String employeeNo;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNo;
    private String photoUrl;
    private String position;

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

    public String getFullName(){
        return this.firstName + " " + this.lastName;
    }
}
