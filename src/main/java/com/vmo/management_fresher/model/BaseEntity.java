package com.vmo.management_fresher.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class BaseEntity implements Serializable {
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void setInitialDate() {
        if(StringUtils.isEmpty(createdBy)){
            throw new PersistenceException("create-by-cannot-be-null");
        }
        if(StringUtils.isEmpty(updatedBy)){
            throw new PersistenceException("update-by-cannot-be-null");
        }
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void updateDate() {
        updatedAt = LocalDateTime.now();
    }
}
