package com.sillador.strecs.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class StudentInformationDTO {

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



}
