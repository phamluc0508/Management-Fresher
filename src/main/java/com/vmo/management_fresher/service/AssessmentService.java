package com.vmo.management_fresher.service;

import com.vmo.management_fresher.dto.response.AssessmentRes;
import com.vmo.management_fresher.model.Assessment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssessmentService {
    AssessmentRes storeFile(String uid, MultipartFile file, Integer assessmentType, Long centerId);
    String deleteFile(Long id);
    Assessment getById(Long id);
    List<AssessmentRes> getAllByCenterId(Long centerId);
}
