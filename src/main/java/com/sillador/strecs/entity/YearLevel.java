package com.sillador.strecs.entity;

import com.sillador.strecs.enums.GroupYearLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "year_levels")
public class YearLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String code; /// this can be used as a course code
    private int levelOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    private YearLevel prerequisiteYear;

    @Enumerated(EnumType.ORDINAL)
    private GroupYearLevel groupYearLevel;

    @Column(columnDefinition = "boolean default false")
    private boolean lastLevel;

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

    public YearLevel(String name){
        this.name = name;
    }

    public YearLevel(){

    }
}
