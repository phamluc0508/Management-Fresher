package com.vmo.management_fresher.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
    Map<String, Object> importFresherToCenter(String uid, MultipartFile file);
}
