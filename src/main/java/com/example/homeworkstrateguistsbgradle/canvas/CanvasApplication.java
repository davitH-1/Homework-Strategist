package com.example.homeworkstrateguistsbgradle.canvas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.example.homeworkstrateguistsbgradle")
@EnableCaching
public class CanvasApplication {
    public static void main(String[] args) {
        SpringApplication.run(CanvasApplication.class, args);
    }
}