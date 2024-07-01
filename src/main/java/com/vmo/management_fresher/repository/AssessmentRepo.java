package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRepo extends JpaRepository<Assessment, Long> {
    Boolean existsByNameFile(String nameFile);
}
