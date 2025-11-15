package com.example.etudiants;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/etudiants")
public class EtudiantController {
    private final EtudiantRepository repository;

    public EtudiantController(EtudiantRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Etudiant> all() { return repository.findAll(); }

    @GetMapping("/{id}")
    public Etudiant getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public Etudiant create(@RequestBody Etudiant e) { return repository.save(e); }

    @PutMapping("/{id}")
    public Etudiant update(@PathVariable Long id, @RequestBody Etudiant e) {
        Etudiant existing = repository.findById(id).orElseThrow();
        existing.setNom(e.getNom());
        existing.setPrenom(e.getPrenom());
        existing.setEmail(e.getEmail());
        existing.setNiveau(e.getNiveau());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}
