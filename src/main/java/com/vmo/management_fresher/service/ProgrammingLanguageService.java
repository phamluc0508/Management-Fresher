package com.vmo.management_fresher.service;

import java.util.List;

import com.vmo.management_fresher.model.ProgrammingLanguage;

public interface ProgrammingLanguageService {
    ProgrammingLanguage createProgrammingLanguage(String uid, ProgrammingLanguage request);

    ProgrammingLanguage updateProgrammingLanguage(String uid, String name, ProgrammingLanguage request);

    String deleteProgrammingLanguage(String name);

    ProgrammingLanguage getById(String name);

    List<ProgrammingLanguage> getAll();
}
