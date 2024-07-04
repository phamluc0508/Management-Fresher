package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.model.AssessmentFresher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentFresherRepo extends JpaRepository<AssessmentFresher, Long> {

    @Query(value = "select a.assessmentType" +
            " from AssessmentFresher af" +
            " inner join Assessment a on a.id = af.assessmentId" +
            " where af.employeeId = :employeeId"
    )
    List<Integer> getAssessmentTypeByEmployeeId(@Param("employeeId") Long employeeId);
}
