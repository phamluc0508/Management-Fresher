package com.vmo.management_fresher.model;

import java.util.Set;

import jakarta.persistence.*;

import com.vmo.management_fresher.base.entity.BaseEntity;

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
public class AssessmentFresher extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double point;
    private Long employeeId;
    private Long assessmentId;

    @ManyToMany
    Set<ProgrammingLanguage> programmingLanguages;
}
