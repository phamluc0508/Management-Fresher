package com.vmo.management_fresher.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import com.vmo.management_fresher.base.entity.BaseEntity;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Role extends BaseEntity {
    @Id
    String name;

    String description;
}
