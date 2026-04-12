package com.example.homeworkstrateguistsbgradle.canvas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching // This turns on the @Cacheable logic we added to the service
@EnableAsync
@EntityScan(basePackages = "com.example.homeworkstrateguistsbgradle.canvas.mysql.entity")
@EnableJpaRepositories(basePackages = "com.example.homeworkstrateguistsbgradle.canvas.mysql.repository")
public class CanvasApplication {
    public static void main(String[] args) {
        SpringApplication.run(CanvasApplication.class, args);
    }
}