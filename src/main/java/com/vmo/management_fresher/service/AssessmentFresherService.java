package com.vmo.management_fresher.service;

import java.util.Map;

import com.vmo.management_fresher.dto.request.PointProgrammingLanguageReq;
import com.vmo.management_fresher.model.AssessmentFresher;

public interface AssessmentFresherService {
    AssessmentFresher addAssessmentFresher(String uid, Long employeeId, Long assessmentId);

    AssessmentFresher updatePointAndProgrammingLanguage(String uid, Long id, PointProgrammingLanguageReq request);

    String deleteAssessmentFresher(String uid, Long id, Boolean allow);

    Map<String, Object> calAverageFresher(String uid, Long employeeId);
}
