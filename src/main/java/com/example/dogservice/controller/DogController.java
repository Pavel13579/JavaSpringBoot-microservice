package com.example.dogservice.controller;

import com.example.dogservice.model.Dog;
import com.example.dogservice.repository.DogRepository;
import com.example.dogservice.exception.DogNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dogs")
public class DogController {

    private final DogRepository repository;

    public DogController(DogRepository repository) {
        this.repository = repository;
    }

    // Összes kutya lekérése
    @GetMapping
    public List<Dog> getAllDogs() {
        return repository.findAll();
    }

    // Egy kutya lekérése ID alapján
    @GetMapping("/{id}")
    public Dog getDogById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new DogNotFoundException(id));
    }

    // Új kutya létrehozása (Validációval)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dog createDog(@Valid @RequestBody Dog dog) {
        return repository.save(dog);
    }

    // Kutya törlése ID alapján
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDog(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new DogNotFoundException(id);
        }
        repository.deleteById(id);
    }
}