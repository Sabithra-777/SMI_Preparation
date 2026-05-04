package com.smartinterview;

import com.smartinterview.service.SeedDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmartInterviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartInterviewApplication.class, args);
    }

    @Bean
    public CommandLineRunner initializeAdmin(SeedDataService seedDataService) {
        return args -> seedDataService.ensureAdminUser();
    }
}
