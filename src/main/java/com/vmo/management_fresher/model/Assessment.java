package com.vmo.management_fresher.model;

import jakarta.persistence.*;

import com.vmo.management_fresher.base.entity.BaseEntity;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Assessment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private Long fileSize;

    @Lob
    private byte[] fileContent;

    private Integer assessmentType;
    private Long centerId;
}
