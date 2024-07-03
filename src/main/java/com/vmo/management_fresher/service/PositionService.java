package com.vmo.management_fresher.service;

import com.vmo.management_fresher.model.Position;
import com.vmo.management_fresher.repository.PositionRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepo repo;

    public void valid(Position request){
        if(request.getName() == null || request.getName().isEmpty()){
            throw new RuntimeException("position-name-not-empty");
        }
    }

    public Position createPosition(String uid, Position request){
        valid(request);

        var exist = repo.findById(request.getName());
        if(exist.isPresent()){
            throw new RuntimeException("position-name-existed");
        }
        Position position = new Position();
        position.setName(request.getName());
        position.setDescription(request.getDescription());
        position.setCreatedBy(uid);
        position.setUpdatedBy(uid);

        return repo.save(position);
    }

    public Position updatePosition(String uid, String name, Position request){
        valid(request);

        Position position = repo.findById(name).orElseThrow(() -> new EntityNotFoundException("position-not-found-with-name: " + name));
        position.setName(request.getName());
        position.setDescription(request.getDescription());
        position.setCreatedBy(uid);
        position.setUpdatedBy(uid);

        return repo.save(position);
    }

    public String deletePosition(String name){
        var exist = repo.findById(name).orElseThrow(() -> new EntityNotFoundException("position-not-found-with-name: " + name));
        repo.deleteById(name);
        return "successfully delete position with id: " + name;
    }

    public Position getById(String name){
        return repo.findById(name).orElseThrow(() -> new EntityNotFoundException("position-not-found-with-name: " + name));
    }

    public List<Position> getAll(){
        return repo.findAll();
    }
}
