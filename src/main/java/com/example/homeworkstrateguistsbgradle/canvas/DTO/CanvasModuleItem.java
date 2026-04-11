package com.example.homeworkstrateguistsbgradle.canvas.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CanvasModuleItem {
    private String title;
    private String type; // Will be "File", "Quiz", "Assignment", etc.

    @JsonProperty("content_id")
    private Long contentId;
}