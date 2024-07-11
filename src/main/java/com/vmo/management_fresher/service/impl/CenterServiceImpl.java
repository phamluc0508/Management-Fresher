package com.vmo.management_fresher.service.impl;

import com.vmo.management_fresher.dto.request.GroupCenterReq;
import com.vmo.management_fresher.model.Center;
import com.vmo.management_fresher.model.EmployeeCenter;
import com.vmo.management_fresher.repository.CenterRepo;
import com.vmo.management_fresher.repository.EmployeeCenterRepo;
import com.vmo.management_fresher.service.AuthenticationService;
import com.vmo.management_fresher.service.CenterService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CenterServiceImpl implements CenterService {
    private final CenterRepo repo;
    private final EmployeeCenterRepo employeeCenterRepo;
    private final AuthenticationService authenticationService;

    private void valid(Center request){
        if(request.getName() == null || request.getName().isEmpty()){
            throw new RuntimeException("center-name-not-blank");
        }
        if(request.getAddress() == null || request.getAddress().isEmpty()){
            throw new RuntimeException("center-address-not-blank");
        }
    }

    @Override
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

    @Override
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

    @Override
    public String deleteCenter(Long id){
        Center center = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("not-found-with-id"));
        if(repo.existsByParentId(id)){
            throw new EntityExistsException("center-is-parent");
        }
        if(employeeCenterRepo.existsByCenterId(id)){
            throw new EntityExistsException("center-has-employees");
        }
        repo.delete(center);
        return "successfully delete center with id:" + id;
    }

    @Override
    public List<Center> getAll(){
        return repo.findAll();
    }

    @Override
    public Center getById(String uid, Long id){
        Center center = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("not-found-with-id"));

        if(!authenticationService.checkAdminRole(uid) && !authenticationService.checkEmployeeCenter(uid, id)){
            throw new AccessDeniedException("no-permission");
        }

        return center;
    }

    @Override
    public Page<Center> search(String name, Pageable pageable){
        return repo.search(name, pageable);
    }

    @Override
    public Center groupTwoCenter(String uid, GroupCenterReq request){
        Center destinationCenter = null;

        if(request.getSourceCenterIds().size() != 2){
            throw new RuntimeException("num-of-source-center-is-two");
        }

        if(request.getDestinationCenterId() != null){
            if(request.getNewCenter() != null){
                throw new RuntimeException("exist-either-destination-center-or-new-center");
            }else {
                if(!request.getSourceCenterIds().remove(request.getDestinationCenterId())){
                    throw new RuntimeException("destination-center-id-not-in-source-center-ids");
                }
                destinationCenter = repo.findById(request.getDestinationCenterId()).orElseThrow(() -> new EntityNotFoundException("center-not-found-with-id: " + request.getDestinationCenterId()));
            }
        }else {
            if(request.getNewCenter() == null){
                throw new RuntimeException("exist-either-destination-center-or-new-center");
            }
        }

        List<Center> centerDelete = repo.findAllById(request.getSourceCenterIds());
        if(centerDelete.size() != request.getSourceCenterIds().size()){
            throw new EntityNotFoundException("center-not-found-with-source-center");
        }

        if(destinationCenter == null){
            destinationCenter = createCenter(uid, request.getNewCenter());
        }

        List<Center> subCenters = repo.findAllByParentIdIn(request.getSourceCenterIds());
        for(Center center : subCenters){
            center.setParentId(null);
            center.setUpdatedBy(uid);
        }
        repo.saveAll(subCenters);

        List<EmployeeCenter> employeeCenters = employeeCenterRepo.findAllByCenterIdIn(request.getSourceCenterIds());
        for(EmployeeCenter employeeCenter : employeeCenters){
            employeeCenter.setCenterId(destinationCenter.getId());
            employeeCenter.setUpdatedBy(uid);
        }
        employeeCenterRepo.saveAll(employeeCenters);

        repo.deleteAll(centerDelete);

        return destinationCenter;
    }
}
