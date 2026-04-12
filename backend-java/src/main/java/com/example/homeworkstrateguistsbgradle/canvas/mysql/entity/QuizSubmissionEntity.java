package com.example.homeworkstrateguistsbgradle.canvas.mysql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_submissions")
@Getter @Setter
public class QuizSubmissionEntity {
    @Id
    private Long id;

    @Column(name = "quiz_id")
    private Long quizId;

    @Column(name = "user_id")
    private Integer userId;

    private Integer attempt;
    private Double score;

    @Column(name = "time_spent")
    private Integer timeSpent; // Seconds

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
}