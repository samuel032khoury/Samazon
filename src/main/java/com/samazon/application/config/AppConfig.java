package com.samazon.application.config;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.samazon.application.services.DataSeedService;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner initData(DataSeedService dataSeedService) {
        return args -> {
            dataSeedService.seedData();
        };
    }
}
