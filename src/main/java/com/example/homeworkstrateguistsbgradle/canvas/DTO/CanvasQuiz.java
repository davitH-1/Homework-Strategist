package com.example.homeworkstrateguistsbgradle.canvas.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CanvasQuiz {
    private Long id;
    private String title;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("quiz_type")
    private String quizType; // assignment, practice_quiz, etc.

    @JsonProperty("time_limit")
    private Integer timeLimit; // in minutes

    @JsonProperty("allowed_attempts")
    private Integer allowedAttempts;

    @JsonProperty("due_at")
    private OffsetDateTime dueAt;

    @JsonProperty("published")
    private boolean published;

    @JsonProperty("question_count")
    private Integer questionCount;

    @JsonProperty("description")
    private String description;
}