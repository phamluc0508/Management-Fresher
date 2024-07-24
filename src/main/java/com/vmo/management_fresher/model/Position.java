package com.vmo.management_fresher.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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
public class Position extends BaseEntity {
    @Id
    String name;

    String description;
}
