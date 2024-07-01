package com.vmo.management_fresher.service;

import com.vmo.management_fresher.model.Center;
import com.vmo.management_fresher.repository.CenterRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CenterService {
    private final CenterRepo repo;

    private void valid(Center request){
        if(request.getName() == null || request.getName().isEmpty()){
            throw new RuntimeException("center-name-not-blank");
        }
        if(request.getAddress() == null || request.getAddress().isEmpty()){
            throw new RuntimeException("center-address-not-blank");
        }
    }

    public Center createCenter (String uid, Center request){
        valid(request);
        var exist = repo.existsByName(request.getName());
        if(exist){
            throw new EntityExistsException("center-name-existed");
        }
        request.setCreatedBy(uid);
        request.setUpdatedBy(uid);

        return repo.save(request);
    }

    public Center updateCenter(String uid, Long id, Center request){
        valid(request);
        var exist = repo.existsByNameAndIdIsNot(request.getName(), id);
        if(exist){
            throw new EntityExistsException("center-name-existed");
        }

        Center center = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("not-found-with-id"));
        center.setName(request.getName());
        center.setAddress(request.getAddress());
        center.setUpdatedBy(uid);
        if(request.getParentId() != null){
            if(request.getParentId() == id){
                throw new RuntimeException("parent-id-not-equal-id");
            }
            var parent = repo.findById(request.getParentId()).orElseThrow(() -> new EntityNotFoundException("parent-not-existed"));
            center.setParentId(request.getParentId());
        }

        return repo.save(center);
    }

    public String deleteCenter(Long id){
        Center center = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("not-found-with-id"));
        boolean exist = repo.existsByParentId(id);
        if(exist){
            throw new EntityExistsException("center-is-parent");
        }
        repo.delete(center);
        return "successfully delete center with id:" + id;
    }

    public List<Center> getAll(){
        return repo.findAll();
    }

    public Center getById(Long id){
        return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("not-found-with-id"));
    }

    public Page<Center> search(String name, Pageable pageable){
        return repo.search(name, pageable);
    }
}
