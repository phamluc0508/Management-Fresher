package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.dto.response.AssessmentRes;
import com.vmo.management_fresher.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepo extends JpaRepository<Assessment, Long> {
    Boolean existsByFileNameAndCenterId(String fileName, Long centerId);

    @Query(value = "select new com.vmo.management_fresher.dto.response.AssessmentRes" +
            "(a.id, a.fileName, null ,a.fileType, a.fileSize, a.assessmentType, a.centerId)" +
            " from Assessment a" +
            " where a.centerId = :centerId"
    )
    List<AssessmentRes> findAllByCenterId(@Param("centerId") Long centerId);
}
