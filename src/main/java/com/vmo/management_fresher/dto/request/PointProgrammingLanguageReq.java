package com.vmo.management_fresher.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointProgrammingLanguageReq {
    private Double point;
    private List<String> programmingLanguages;
}
