package com.example.homeworkstrateguistsbgradle.canvas.mysql.repository;

import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    // Find courses belonging to a specific user via their Google Token
    @Query("SELECT c FROM CourseEntity c JOIN UserEntity u ON c.userId = u.id WHERE u.googleToken = :token")
    List<CourseEntity> findByGoogleToken(@Param("token") String token);
}