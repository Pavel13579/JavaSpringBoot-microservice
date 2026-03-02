package com.example.dogservice.service;

import com.example.dogservice.model.Dog;
import com.example.dogservice.repository.DogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class DogImageScheduler {

    private final DogRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    public DogImageScheduler(DogRepository repository) {
        this.repository = repository;
    }

    /**
     * Fix intervallumonként futó feladat. 
     * A példában 1 percenként (60000 ms), 
     * de a feladat szerinti óránkénti futáshoz: (fixedRate = 3600000)
     */
    @Scheduled(fixedRate = 60000) 
    public void updateMissingDogImage() {
        // 1. Keresünk 1 kutyát az adatbázisban, akinek nincs képe
        Optional<Dog> dogWithoutImage = repository.findAll().stream()
                .filter(dog -> dog.getImage() == null || dog.getImage().isEmpty())
                .findFirst();

        if (dogWithoutImage.isPresent()) {
            Dog dog = dogWithoutImage.get();
            log.info("Kép keresése a következő kutyának: {}", dog.getName());

            try {
                // 2. Meghívjuk a Dog API-t egy random képért
                Map<String, Object> response = restTemplate.getForObject(
                    "https://dog.ceo/api/breeds/image/random", Map.class);

                if (response != null && "success".equals(response.get("status"))) {
                    // 3. Frissítjük a kutya image oszlopát az URL-lel
                    String imageUrl = (String) response.get("message");
                    dog.setImage(imageUrl);

                    // 4. Elmentjük a frissített kutyát az adatbázisba
                    repository.save(dog);
                    log.info("Sikeres frissítés: {} mostantól rendelkezik képpel.", dog.getName());
                }
            } catch (Exception e) {
                log.error("Hiba történt az API hívás során: {}", e.getMessage());
            }
        } else {
            log.info("Minden kutyának van már képe, nincs tennivaló.");
        }
    }
}