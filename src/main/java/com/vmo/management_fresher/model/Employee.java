package com.vmo.management_fresher.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.vmo.management_fresher.base.entity.BaseEntity;
import com.vmo.management_fresher.dto.request.EmployeeReq;
import com.vmo.management_fresher.dto.response.EmployeeRes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String accountId;

    public static Employee of(String uid, EmployeeReq request) {
        return Employee.builder()
                .id(request.getId())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .createdBy(uid)
                .updatedBy(uid)
                .build();
    }

    public static EmployeeRes toRES(Employee request) {
        return EmployeeRes.builder()
                .id(request.getId())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .accountId(request.getAccountId())
                .build();
    }

    public EmployeeRes toRES() {
        return toRES(this);
    }
    ;
}
