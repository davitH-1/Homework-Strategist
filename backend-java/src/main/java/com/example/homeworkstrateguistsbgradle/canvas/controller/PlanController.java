package com.example.homeworkstrateguistsbgradle.canvas.controller;

import com.example.homeworkstrateguistsbgradle.canvas.DTO.PlanResponseDTO;
import com.example.homeworkstrateguistsbgradle.canvas.service.PlanForwardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/plan")
@CrossOrigin(origins = "http://localhost:4200")
public class PlanController {

    private final PlanForwardService planForwardService;

    public PlanController(PlanForwardService planForwardService) {
        this.planForwardService = planForwardService;
    }

    /**
     * Fetches Canvas assignments for the user, forwards them to the Python
     * backend for AI planning + calendar scheduling, and returns the result.
     *
     * Body: { "userId": "<google sub>" }
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generatePlan(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body("userId is required");
        }
        try {
            PlanResponseDTO result = planForwardService.generatePlan(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Plan generation failed: " + e.getMessage());
        }
    }
}