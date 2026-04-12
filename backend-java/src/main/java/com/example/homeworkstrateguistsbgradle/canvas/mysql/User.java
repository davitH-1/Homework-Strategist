package com.example.homeworkstrateguistsbgradle.canvas.mysql; // Change to your actual package path

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "user", schema = "ai_planner")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "google_token", unique = true, nullable = false, length = 512)
    private String googleToken;

    @Column(name = "ivc_token", length = 512)
    private String ivcToken; // This holds your Canvas Access Token

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "status_active", nullable = false)
    private boolean statusActive = true;
}