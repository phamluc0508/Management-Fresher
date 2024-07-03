package com.vmo.management_fresher.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRes {

    private Long id;
    private String fileName;
    private String downloadURL;
    private String fileType;
    private Long fileSize;
    private Integer assessmentType;
    private Long centerId;
}
