package com.vmo.management_fresher.service;

import com.vmo.management_fresher.constant.Constant;
import com.vmo.management_fresher.dto.response.ResAssessment;
import com.vmo.management_fresher.model.Assessment;
import com.vmo.management_fresher.repository.AssessmentRepo;
import com.vmo.management_fresher.repository.CenterRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AssessmentService {
    private final AssessmentRepo repo;
    private final CenterRepo centerRepo;

    public ResAssessment storeFile(String uid, MultipartFile file, Integer assessmentType, Long centerId){
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        var exist = repo.existsByFileName(fileName);
        if(exist){
            throw new EntityExistsException("name-file-existed");
        }
        Assessment assessment = new Assessment();
        assessment.setFileName(fileName);
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

        return new ResAssessment(assessment.getFileName(),
                downloadURL,
                file.getContentType(),
                file.getSize());
    }

    public void deleteFile(Long id){
        Assessment assessment = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("not-found-with-id: " + id));
        repo.delete(assessment);
    }

    public Assessment getById(Long id){
        return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("not-found-with-id: " + id));
    }
}
