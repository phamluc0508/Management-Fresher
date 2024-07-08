package com.vmo.management_fresher.service;

import com.vmo.management_fresher.dto.request.GroupCenterReq;
import com.vmo.management_fresher.model.Center;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CenterService {
    Center createCenter (String uid, Center request);
    Center updateCenter(String uid, Long id, Center request);
    String deleteCenter(Long id);
    List<Center> getAll();
    Center getById(Long id);
    Page<Center> search(String name, Pageable pageable);
    Center groupTwoCenter(String uid, GroupCenterReq request);
}
