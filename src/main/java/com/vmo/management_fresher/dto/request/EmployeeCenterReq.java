package com.vmo.management_fresher.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCenterReq {
    private Long id;
    private Long employeeId;
    private Long centerId;
    private String position;
}
