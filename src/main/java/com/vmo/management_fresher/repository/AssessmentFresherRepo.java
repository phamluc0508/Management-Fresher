package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.model.AssessmentFresher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AssessmentFresherRepo extends JpaRepository<AssessmentFresher, Long> {

    @Query(value = "select a.assessmentType" +
            " from AssessmentFresher af" +
            " inner join Assessment a on a.id = af.assessmentId" +
            " where af.employeeId = :employeeId"
    )
    List<Integer> getAssessmentTypeByEmployeeId(@Param("employeeId") Long employeeId);

    @Query(value = "select a.assessmentType as type" +
            " , af.point as point" +
            " from AssessmentFresher af" +
            " inner join Assessment a on a.id = af.assessmentId" +
            " where af.employeeId = :employeeId"
    )
    List<Map<String, Object>> getPointByEmployeeId(@Param("employeeId")Long employeeId);
}
