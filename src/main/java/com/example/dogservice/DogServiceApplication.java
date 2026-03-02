package com.example.dogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Ez elengedhetetlen a későbbi Scheduled Task feladathoz
public class DogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DogServiceApplication.class, args);
    }
}