package com.example.homeworkstrateguistsbgradle.canvas.mysql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter @Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "google_token", unique = true, nullable = true)
    private String googleToken;

    @Column(name = "ivc_token")
    private String ivcToken; // This fixes the .getIvcToken() error

    @Column(name = "status_active")
    private boolean statusActive = true;
}