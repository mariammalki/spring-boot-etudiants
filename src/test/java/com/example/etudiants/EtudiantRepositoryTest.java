package com.example.etudiants;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EtudiantRepositoryTest {

    @Autowired
    private EtudiantRepository repository;

    @Test
    void testSaveEtudiant() {
        Etudiant e = new Etudiant();
        e.setNom("Doe");
        e.setPrenom("John");
        e.setEmail("john.doe@example.com");
        e.setNiveau("Licence");
        repository.save(e);
        assertNotNull(e.getId());
    }
}
