package com.vmo.management_fresher.service.impl;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.dto.response.AssessmentRes;
import com.vmo.management_fresher.model.Assessment;
import com.vmo.management_fresher.model.AssessmentFresher;
import com.vmo.management_fresher.repository.AssessmentFresherRepo;
import com.vmo.management_fresher.repository.AssessmentRepo;
import com.vmo.management_fresher.repository.CenterRepo;
import com.vmo.management_fresher.service.AssessmentService;
import com.vmo.management_fresher.service.AuthenticationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {
    private final AssessmentRepo repo;
    private final CenterRepo centerRepo;
    private final AssessmentFresherRepo assessmentFresherRepo;
    private final AuthenticationService authenticationService;

    @Override
    public AssessmentRes storeFile(String uid, MultipartFile file, Integer assessmentType, Long centerId){
        if(!authenticationService.checkAdminRole(uid) && !authenticationService.checkDirectorCenter(uid, centerId)){
            throw new AccessDeniedException("no-permission");
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        var exist = repo.existsByFileNameAndCenterId(fileName, centerId);
        if(exist){
            throw new EntityExistsException("name-file-existed");
        }
        Assessment assessment = new Assessment();
        assessment.setFileName(fileName);
        assessment.setFileSize(file.getSize());
        assessment.setFileType(file.getContentType());
        try {
            assessment.setFileContent(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (assessmentType != Constant.ASSESSMENT_OTHER &&
                assessmentType != Constant.ASSESSMENT_LEVEL_1 &&
                assessmentType != Constant.ASSESSMENT_LEVEL_2 &&
                assessmentType != Constant.ASSESSMENT_LEVEL_3) {
            throw new IllegalArgumentException("invalid-type-assessment-value: " + assessmentType);
        }
        assessment.setAssessmentType(assessmentType);
        if(!centerRepo.existsById(centerId)){
            throw new IllegalArgumentException("center-exist-with-id: " + centerId);
        }
        assessment.setCenterId(centerId);
        assessment.setCreatedBy(uid);
        assessment.setUpdatedBy(uid);

        String downloadURL = "";
        assessment = repo.save(assessment);

        downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/assessment/download/")
                .path(String.valueOf(assessment.getId())).toUriString();

        return new AssessmentRes(assessment.getId(),
                assessment.getFileName(),
                downloadURL,
                file.getContentType(),
                file.getSize(),
                assessmentType,
                centerId);
    }

    @Override
    public String deleteFile(String uid, Long id){
        Assessment assessment = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("not-found-with-id: " + id));

        if(!authenticationService.checkAdminRole(uid) && !authenticationService.checkDirectorCenter(uid, assessment.getCenterId())){
            throw new AccessDeniedException("no-permission");
        }

        if(assessmentFresherRepo.existsByAssessmentId(id)){
            throw new EntityExistsException("assessment-assign-fresher");
        }
        repo.delete(assessment);

        return "Success";
    }

    @Override
    public Assessment getById(String uid, Long id){
        Assessment assessment = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("not-found-with-id: " + id));
        AssessmentFresher assessmentFresher = assessmentFresherRepo.findByAssessmentId(id).orElseThrow(() -> new EntityNotFoundException("assessment-fresher-not-found"));
        if(!authenticationService.checkAdminRole(uid)
                && !authenticationService.checkDirectorCenter(uid, assessment.getCenterId())
                && !authenticationService.checkIsMyself(uid, assessmentFresher.getEmployeeId())){
            throw new AccessDeniedException("no-permission");
        }
        return assessment;
    }

    @Override
    public List<AssessmentRes> getAllByCenterId(String uid, Long centerId){
        if(!authenticationService.checkAdminRole(uid) && !authenticationService.checkDirectorCenter(uid, centerId)){
            throw new AccessDeniedException("no-permission");
        }
        List<AssessmentRes> result = repo.findAllByCenterId(centerId);
        for(var r : result){
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/assessment/download/")
                    .path(String.valueOf(r.getId())).toUriString();
            r.setDownloadURL(downloadURL);
        }
        return result;
    }
}
