package com.vmo.management_fresher.service;

import com.vmo.management_fresher.dto.AssessmentDTO;
import com.vmo.management_fresher.model.Assessment;
import com.vmo.management_fresher.repository.AssessmentRepo;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AssessmentService {
    private final AssessmentRepo repo;

    public void storeFile(String uid, AssessmentDTO request){
        MultipartFile file = request.getFile();
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
        assessment.setTypeAssessment(request.getTypeAssessment());
        assessment.setCenterId(request.getCenterId());
        assessment.setCreatedBy(uid);
        assessment.setUpdatedBy(uid);


    }
}
