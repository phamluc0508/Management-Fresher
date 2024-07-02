package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.dto.response.ResAssessment;
import com.vmo.management_fresher.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepo extends JpaRepository<Assessment, Long> {
    Boolean existsByFileName(String fileName);

    @Query(value = "select new com.vmo.management_fresher.dto.response.ResAssessment" +
            "(a.id, a.fileName, null ,a.fileType, a.fileSize, a.assessmentType, a.centerId)" +
            " from Assessment a" +
            " where a.centerId = :centerId"
    )
    List<ResAssessment> findAllByCenterId(@Param("centerId") Long centerId);
}
