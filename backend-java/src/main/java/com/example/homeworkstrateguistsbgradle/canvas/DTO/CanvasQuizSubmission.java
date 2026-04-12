package com.example.homeworkstrateguistsbgradle.canvas.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CanvasQuizSubmission {
    private Long id;

    @JsonProperty("quiz_id")
    private Long quizId;

    @JsonProperty("user_id")
    private Integer userId;

    private Integer attempt;

    private Double score;

    @JsonProperty("time_spent")
    private Integer timeSpent; // This is the value you need (in seconds)

    @JsonProperty("finished_at")
    private OffsetDateTime finishedAt;
}