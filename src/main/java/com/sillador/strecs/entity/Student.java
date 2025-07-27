package com.sillador.strecs.entity;

import com.sillador.strecs.enums.Gender;
import com.sillador.strecs.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique = true, nullable = false)
    private String studentId;
    private String lrn;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;
    private Date birthday;
    private String email;
    private String address;
    @Column(columnDefinition = "VARCHAR(20)")
    private String contactNumber;
    @Column(columnDefinition = "TEXT")
    private String photoUrl;
    @Enumerated(EnumType.ORDINAL)
    private StudentStatus status;
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    @OneToOne(fetch = FetchType.LAZY)
    private StudentInformation information;

    public String getFullNameStr() {
        return firstName + " " + lastName;
    }


    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
