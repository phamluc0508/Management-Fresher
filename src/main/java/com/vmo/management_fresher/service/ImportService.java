package com.vmo.management_fresher.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImportService {
    Map<String, Object> importFresherToCenter(String uid, MultipartFile file);
}
