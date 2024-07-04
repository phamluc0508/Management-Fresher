package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.model.ProgrammingLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgrammingLanguageRepo extends JpaRepository<ProgrammingLanguage, String> {
}
