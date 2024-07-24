package com.vmo.management_fresher.service;

import java.util.List;

import com.vmo.management_fresher.model.Position;

public interface PositionService {
    Position createPosition(String uid, Position request);

    Position updatePosition(String uid, String name, Position request);

    String deletePosition(String name);

    Position getById(String name);

    List<Position> getAll();
}
