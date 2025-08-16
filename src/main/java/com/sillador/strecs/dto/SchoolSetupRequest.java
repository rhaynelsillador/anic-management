package com.sillador.strecs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolSetupRequest {
    private String schoolName;
    private String systemName;
    private String address;
    private String contactInformation;
    private String logoPath;
}
