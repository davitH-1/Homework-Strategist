package com.example.homeworkstrateguistsbgradle.canvas.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanResponseDTO {
    private String userId;
    private String message;
    private List<String> scheduledEvents;
}