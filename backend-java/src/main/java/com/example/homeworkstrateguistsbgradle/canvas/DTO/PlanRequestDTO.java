package com.example.homeworkstrateguistsbgradle.canvas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlanRequestDTO {
    private String userId;
    private List<PlanClassItemDTO> classes;
}