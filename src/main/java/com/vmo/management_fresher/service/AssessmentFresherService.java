package com.vmo.management_fresher.service;

import com.vmo.management_fresher.dto.request.PointProgrammingLanguageReq;
import com.vmo.management_fresher.model.AssessmentFresher;

import java.util.Map;

public interface AssessmentFresherService {
    AssessmentFresher addAssessmentFresher(String uid, Long employeeId, Long assessmentId);
    AssessmentFresher updatePointAndProgrammingLanguage(String uid, Long id, PointProgrammingLanguageReq request);
    String deleteAssessmentFresher(Long id, Boolean allow);
    Map<String,Object> calAverageFresher(Long employeeId);
}
