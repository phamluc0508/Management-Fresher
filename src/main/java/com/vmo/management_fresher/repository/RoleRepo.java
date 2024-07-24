package com.vmo.management_fresher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vmo.management_fresher.model.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, String> {}
