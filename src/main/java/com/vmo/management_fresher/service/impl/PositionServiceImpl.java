package com.vmo.management_fresher.service.impl;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.vmo.management_fresher.model.Position;
import com.vmo.management_fresher.repository.PositionRepo;
import com.vmo.management_fresher.service.PositionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
    private final PositionRepo repo;

    private void valid(Position request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new RuntimeException("position-name-not-empty");
        }
    }

    @Override
    public Position createPosition(String uid, Position request) {
        valid(request);

        var exist = repo.findById(request.getName());
        if (exist.isPresent()) {
            throw new RuntimeException("position-name-existed");
        }
        Position position = new Position();
        position.setName(request.getName());
        position.setDescription(request.getDescription());
        position.setCreatedBy(uid);
        position.setUpdatedBy(uid);

        return repo.save(position);
    }

    @Override
    public Position updatePosition(String uid, String name, Position request) {
        valid(request);

        Position position = repo.findById(name)
                .orElseThrow(() -> new EntityNotFoundException("position-not-found"));
        position.setName(request.getName());
        position.setDescription(request.getDescription());
        position.setUpdatedBy(uid);

        return repo.save(position);
    }

    @Override
    public String deletePosition(String name) {
        var exist = repo.findById(name)
                .orElseThrow(() -> new EntityNotFoundException("position-not-found"));
        repo.delete(exist);
        return "successfully delete position with id: " + name;
    }

    @Override
    public Position getById(String name) {
        return repo.findById(name)
                .orElseThrow(() -> new EntityNotFoundException("position-not-found"));
    }

    @Override
    public List<Position> getAll() {
        return repo.findAll();
    }
}
