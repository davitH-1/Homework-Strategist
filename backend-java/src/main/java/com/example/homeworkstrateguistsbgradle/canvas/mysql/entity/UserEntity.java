package com.example.homeworkstrateguistsbgradle.canvas.mysql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter @Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "google_token", unique = true, nullable = false)
    private String googleToken;

    @Column(name = "ivc_token")
    private String ivcToken;

    @Column(name = "status_active")
    private boolean statusActive = true;
}