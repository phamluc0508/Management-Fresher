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
public class Center extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "email", unique = true)
    private String name;

    @Column
    private String address;

    @Column
    private Long parentId;
}
