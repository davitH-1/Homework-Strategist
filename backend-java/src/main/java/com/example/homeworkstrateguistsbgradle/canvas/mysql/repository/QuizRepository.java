package com.example.homeworkstrateguistsbgradle.canvas.mysql.repository;

import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, Long> {}
