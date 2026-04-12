package com.example.homeworkstrateguistsbgradle.canvas.mysql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "quizzes")
public class QuizEntity {
    // Getters and Setters
    @Setter
    @Getter
    @Id private Long id;
    @Setter
    @Getter
    @Column(name = "course_id") private Long courseId;
    @Setter
    @Getter
    private String title;
    @Setter
    @Getter
    @Column(name = "html_url") private String htmlUrl;
    @Setter
    @Getter
    @Column(name = "question_count") private Integer questionCount;
    @Setter
    @Getter
    @Column(columnDefinition = "LONGTEXT")
    private String description; // The HTML instructions
    @Setter
    @Getter
    @Column(name = "time_limit")
    private Integer timeLimit; // In minutes
    @Setter
    @Getter
    @Column(name = "quiz_type")
    private String quizType; // assignment, practice_quiz, etc.
    // Inside QuizEntity.java
    @Setter
    @Getter
    @Column(name = "due_at")
    private LocalDateTime dueAt;

}
