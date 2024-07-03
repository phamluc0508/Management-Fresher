package com.vmo.management_fresher.service;

import com.vmo.management_fresher.model.Role;
import com.vmo.management_fresher.repository.RoleRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepo repo;

    public void valid(Role request){
        if(request.getName() == null || request.getName().isEmpty()){
            throw new RuntimeException("role-name-not-empty");
        }
    }

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

    public Role updateRole(String uid, String name, Role request){
        valid(request);

        Role role = repo.findById(name).orElseThrow(() -> new EntityNotFoundException("role-not-found-with-name: " + name));
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setCreatedBy(uid);
        role.setUpdatedBy(uid);

        return repo.save(role);
    }

    public String deleteRole(String name){
        var exist = repo.findById(name).orElseThrow(() -> new EntityNotFoundException("role-not-found-with-name: " + name));
        repo.deleteById(name);
        return "successfully delete role with id: " + name;
    }

    public Role getById(String name){
        return repo.findById(name).orElseThrow(() -> new EntityNotFoundException("role-not-found-with-name: " + name));
    }

    public List<Role> getAll(){
        return repo.findAll();
    }
}
