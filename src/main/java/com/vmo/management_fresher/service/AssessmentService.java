package com.vmo.management_fresher.service;

import com.vmo.management_fresher.constant.Constant;
import com.vmo.management_fresher.model.Assessment;
import com.vmo.management_fresher.repository.AssessmentRepo;
import com.vmo.management_fresher.repository.CenterRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AssessmentService {
    private final AssessmentRepo repo;
    private final CenterRepo centerRepo;

    public void storeFile(String uid, MultipartFile file, Integer typeAssessment, Long centerId){
        String nameFile = file.getOriginalFilename();

        var exist = repo.existsByNameFile(nameFile);
        if(exist){
            throw new EntityExistsException("name-file-existed");
        }
        Assessment assessment = new Assessment();
        assessment.setNameFile(nameFile);
        assessment.setTypeFile(file.getContentType());
        try {
            assessment.setContentFile(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (typeAssessment != Constant.ASSESSMENT_OTHER &&
                typeAssessment != Constant.ASSESSMENT_LEVEL_1 &&
                typeAssessment != Constant.ASSESSMENT_LEVEL_2 &&
                typeAssessment != Constant.ASSESSMENT_LEVEL_3) {
            throw new IllegalArgumentException("Invalid type-assessment value: " + typeAssessment);
        }
        assessment.setTypeAssessment(typeAssessment);
        if(!centerRepo.existsById(centerId)){
            throw new IllegalArgumentException("center-exist-with-id: " + centerId);
        }
        assessment.setCenterId(centerId);
        assessment.setCreatedBy(uid);
        assessment.setUpdatedBy(uid);

        repo.save(assessment);
    }

    public void deleteFile(Long id){
        Assessment assessment = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("not-found-with-id: " + id));
        repo.delete(assessment);
    }

    public Assessment getById(Long id){
        return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("not-found-with-id: " + id));
    }
}
