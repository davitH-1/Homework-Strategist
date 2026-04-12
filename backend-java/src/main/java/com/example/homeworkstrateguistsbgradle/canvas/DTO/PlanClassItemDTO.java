package com.example.homeworkstrateguistsbgradle.canvas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanClassItemDTO {
    private String name;
    private String dueDate;        // YYYY-MM-DD
    private double estimatedHours;
}