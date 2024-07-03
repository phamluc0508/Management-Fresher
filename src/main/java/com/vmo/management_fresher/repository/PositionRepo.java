package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepo extends JpaRepository<Position, String> {
}
