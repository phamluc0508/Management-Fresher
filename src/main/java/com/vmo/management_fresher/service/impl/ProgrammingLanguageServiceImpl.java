package com.vmo.management_fresher.service.impl;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.vmo.management_fresher.model.ProgrammingLanguage;
import com.vmo.management_fresher.repository.ProgrammingLanguageRepo;
import com.vmo.management_fresher.service.ProgrammingLanguageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgrammingLanguageServiceImpl implements ProgrammingLanguageService {
    private final ProgrammingLanguageRepo repo;

    private void valid(ProgrammingLanguage request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new RuntimeException("programming-language-name-not-empty");
        }
    }

    @Override
    public ProgrammingLanguage createProgrammingLanguage(String uid, ProgrammingLanguage request) {
        valid(request);

        var exist = repo.findById(request.getName());
        if (exist.isPresent()) {
            throw new RuntimeException("programming-language-name-existed");
        }
        ProgrammingLanguage programmingLanguage = new ProgrammingLanguage();
        programmingLanguage.setName(request.getName());
        programmingLanguage.setDescription(request.getDescription());
        programmingLanguage.setCreatedBy(uid);
        programmingLanguage.setUpdatedBy(uid);

        return repo.save(programmingLanguage);
    }

    @Override
    public ProgrammingLanguage updateProgrammingLanguage(String uid, String name, ProgrammingLanguage request) {
        valid(request);

        ProgrammingLanguage programmingLanguage = repo.findById(name)
                .orElseThrow(() -> new EntityNotFoundException("programming-language-not-found-with-name: " + name));
        programmingLanguage.setName(request.getName());
        programmingLanguage.setDescription(request.getDescription());
        programmingLanguage.setUpdatedBy(uid);

        return repo.save(programmingLanguage);
    }

    @Override
    public String deleteProgrammingLanguage(String name) {
        var exist = repo.findById(name)
                .orElseThrow(() -> new EntityNotFoundException("programming-language-not-found-with-name: " + name));
        repo.deleteById(name);
        return "successfully delete programmingLanguage with id: " + name;
    }

    @Override
    public ProgrammingLanguage getById(String name) {
        return repo.findById(name)
                .orElseThrow(() -> new EntityNotFoundException("programming-language-not-found-with-name: " + name));
    }

    @Override
    public List<ProgrammingLanguage> getAll() {
        return repo.findAll();
    }
}
