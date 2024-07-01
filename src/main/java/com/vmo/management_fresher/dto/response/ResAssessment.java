package com.vmo.management_fresher.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResAssessment {

    private String fileName;
    private String downloadURL;
    private String fileType;
    private Long fileSize;
}
