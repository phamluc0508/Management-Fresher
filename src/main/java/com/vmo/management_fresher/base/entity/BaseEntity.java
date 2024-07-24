package com.vmo.management_fresher.base.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public abstract class BaseEntity implements Serializable {
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void setInitialDate() {
        if (StringUtils.isEmpty(createdBy)) {
            throw new PersistenceException("create-by-cannot-be-null");
        }
        if (StringUtils.isEmpty(updatedBy)) {
            throw new PersistenceException("update-by-cannot-be-null");
        }
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void updateDate() {
        updatedAt = LocalDateTime.now();
    }
}
