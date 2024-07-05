package com.vmo.management_fresher.service.impl;

import com.vmo.management_fresher.model.Role;
import com.vmo.management_fresher.repository.RoleRepo;
import com.vmo.management_fresher.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepo repo;

    private void valid(Role request){
        if(request.getName() == null || request.getName().isEmpty()){
            throw new RuntimeException("role-name-not-empty");
        }
    }

    @Override
    public Role createRole(String uid, Role request){
        valid(request);

        var exist = repo.findById(request.getName());
        if(exist.isPresent()){
            throw new RuntimeException("role-name-existed");
        }
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setCreatedBy(uid);
        role.setUpdatedBy(uid);

        return repo.save(role);
    }

    @Override
    public Role updateRole(String uid, String name, Role request){
        valid(request);

        Role role = repo.findById(name).orElseThrow(() -> new EntityNotFoundException("role-not-found-with-name: " + name));
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setUpdatedBy(uid);

        return repo.save(role);
    }

    @Override
    public String deleteRole(String name){
        var exist = repo.findById(name).orElseThrow(() -> new EntityNotFoundException("role-not-found-with-name: " + name));
        repo.deleteById(name);
        return "successfully delete role with id: " + name;
    }

    @Override
    public Role getById(String name){
        return repo.findById(name).orElseThrow(() -> new EntityNotFoundException("role-not-found-with-name: " + name));
    }

    @Override
    public List<Role> getAll(){
        return repo.findAll();
    }
}
