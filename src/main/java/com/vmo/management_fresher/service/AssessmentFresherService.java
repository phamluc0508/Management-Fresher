package com.vmo.management_fresher.service;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.dto.request.PointProgrammingLanguageReq;
import com.vmo.management_fresher.model.Assessment;
import com.vmo.management_fresher.model.AssessmentFresher;
import com.vmo.management_fresher.model.ProgrammingLanguage;
import com.vmo.management_fresher.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssessmentFresherService {
    private final AssessmentFresherRepo repo;
    private final EmployeeCenterRepo employeeCenterRepo;
    private final AssessmentRepo assessmentRepo;
    private final ProgrammingLanguageRepo programmingLanguageRepo;

    public AssessmentFresher addAssessmentFresher(String uid, Long employeeId, Long assessmentId){
        Assessment assessment = assessmentRepo.findById(assessmentId).orElseThrow(() -> new EntityNotFoundException("assessment-not-found-with-id: " + assessmentId));

        if(!employeeCenterRepo.existsByEmployeeIdAndCenterIdAndPositionName(employeeId, assessment.getCenterId(), Constant.FRESHER_POSITION)){
            throw new EntityExistsException("employee-is-not-fresher-in-the-center");
        }

        List<Integer> assessmentTypes = repo.getAssessmentTypeByEmployeeId(employeeId);
        for(Integer type : assessmentTypes){
            if(type == assessment.getAssessmentType()){
                throw new RuntimeException("assessment-type-existed");
            }
        }

        AssessmentFresher assessmentFresher = new AssessmentFresher();
        assessmentFresher.setAssessmentId(assessmentId);
        assessmentFresher.setEmployeeId(employeeId);
        assessmentFresher.setCreatedBy(uid);
        assessmentFresher.setUpdatedBy(uid);

        return repo.save(assessmentFresher);
    }

    public AssessmentFresher updatePointAndProgrammingLanguage(String uid, Long id, PointProgrammingLanguageReq request){
        AssessmentFresher assessmentFresher = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("assessment-fresher-not-found-with-id: " + id));
        if(request.getPoint() != null){
            assessmentFresher.setPoint(request.getPoint());
        }
        if(request.getProgrammingLanguages() != null && request.getProgrammingLanguages().size()>0){
            List<ProgrammingLanguage> programmingLanguages = programmingLanguageRepo.findAllById(request.getProgrammingLanguages());
            if(programmingLanguages.size() == 0){
                throw new RuntimeException("invalid-programming-format");
            }
            assessmentFresher.setProgrammingLanguages(new HashSet<>(programmingLanguages));
        }
        assessmentFresher.setUpdatedBy(uid);

        return repo.save(assessmentFresher);
    }

    public String deleteAssessmentFresher(Long id, Boolean allow){
        AssessmentFresher assessmentFresher = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("assessment-fresher-not-found-with-id: " + id));
        if(assessmentFresher.getPoint() != null && !allow){
            throw new RuntimeException("assessment-fresher-cannot-delete");
        }
        repo.delete(assessmentFresher);
        return "Success!";
    }

    public Map<String,Object> calAverageFresher(Long employeeId){

        Double point1 = 0.0, point2 = 0.0, point3 = 0.0, average = 0.0;

        var pointList = repo.getPointByEmployeeId(employeeId);
        for(var p : pointList){
            if(Constant.ASSESSMENT_LEVEL_1 == (int)p.get("type")){
                point1 = (Double) p.get("point");
            } else if(Constant.ASSESSMENT_LEVEL_2 == (int)p.get("type")){
                point2 = (Double) p.get("point");
            } else if(Constant.ASSESSMENT_LEVEL_3 == (int)p.get("type")){
                point3 = (Double) p.get("point");
            }
        }

        if(point1 > 0 && point2 > 0 && point3 > 0){
            average = (point1 + point2 + point3) / 3;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("point1", point1);
        result.put("point2", point2);
        result.put("point3", point3);
        result.put("average", average);

        return result;
    }
}
