package com.example.homeworkstrateguistsbgradle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class HomeworkStrateguistSbGradleApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomeworkStrateguistSbGradleApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}