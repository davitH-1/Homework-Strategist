package com.example.homeworkstrateguistsbgradle.canvas.mysql.repository;

import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> { // Change Long to Integer
    Optional<UserEntity> findByGoogleToken(String googleToken);
    Optional<UserEntity> findByIvcToken(String ivcToken);
}