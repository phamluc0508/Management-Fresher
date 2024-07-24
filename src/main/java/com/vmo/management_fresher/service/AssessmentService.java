package com.vmo.management_fresher.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.vmo.management_fresher.dto.response.AssessmentRes;
import com.vmo.management_fresher.model.Assessment;

public interface AssessmentService {
    AssessmentRes storeFile(String uid, MultipartFile file, Integer assessmentType, Long centerId);

    String deleteFile(String uid, Long id);

    Assessment getById(String uid, Long id);

    List<AssessmentRes> getAllByCenterId(String uid, Long centerId);
}
