package com.example.homeworkstrateguistsbgradle.canvas.mysql.repository;

import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.QuizEntity;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.QuizSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmissionEntity, Long> {}