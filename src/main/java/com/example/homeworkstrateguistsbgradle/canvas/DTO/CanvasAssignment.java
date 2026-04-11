package com.example.homeworkstrateguistsbgradle.canvas.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CanvasAssignment {
    private Long id;
    private String name;

    @JsonProperty("due_at")
    private OffsetDateTime dueAt;

    @JsonProperty("course_id")
    private Long courseId;

    private String description;
}
