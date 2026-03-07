package com.example.dogservice.service;

import com.example.dogservice.model.Dog;
import com.example.dogservice.repository.DogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Optional;

@Service
public class DogImageScheduler {

    private static final Logger log = LoggerFactory.getLogger(DogImageScheduler.class);

    private final DogRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    public DogImageScheduler(DogRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 60000) 
    public void updateMissingDogImage() {
        Optional<Dog> dogWithoutImage = repository.findAll().stream()
                .filter(dog -> dog.getImage() == null || dog.getImage().isEmpty())
                .findFirst();

        if (dogWithoutImage.isPresent()) {
            Dog dog = dogWithoutImage.get();
            log.info("Kép keresése a következő kutyának: {}", dog.getName());

            try {
                Map<String, Object> response = restTemplate.getForObject(
                    "https://dog.ceo/api/breeds/image/random", Map.class);

                if (response != null && "success".equals(response.get("status"))) {
                    String imageUrl = (String) response.get("message");
                    dog.setImage(imageUrl);
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