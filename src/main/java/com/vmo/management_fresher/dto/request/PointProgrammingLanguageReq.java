package com.vmo.management_fresher.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointProgrammingLanguageReq {
    private Double point;
    private List<String> programmingLanguages;
}
