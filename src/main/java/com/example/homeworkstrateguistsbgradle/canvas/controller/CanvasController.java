package com.example.homeworkstrateguistsbgradle.canvas.controller;

import com.example.homeworkstrateguistsbgradle.canvas.DTO.CanvasAssignment;
import com.example.homeworkstrateguistsbgradle.canvas.DTO.CanvasCourse;
import com.example.homeworkstrateguistsbgradle.canvas.DTO.CanvasUserProfile;
import com.example.homeworkstrateguistsbgradle.canvas.service.CanvasApiService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/canvas")
@CrossOrigin(origins = "*")
public class CanvasController {

    private final CanvasApiService canvasService;

    public CanvasController(CanvasApiService canvasService) {
        this.canvasService = canvasService;
    }

    @GetMapping("/courses")
    public List<CanvasCourse> getCourses() {
        // No headers needed in the request anymore!
        // The service already has the token from @Value.
        return canvasService.getCourses();
    }

    @GetMapping("/assignments/{courseId}")
    public List<CanvasAssignment> getAssignments(@PathVariable Long courseId) {
        return canvasService.getAssignments(courseId);
    }

    @GetMapping("/courses/{courseId}/assignments/{assignmentId}")
    public CanvasAssignment getAssignmentDetails(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId) {
        return canvasService.getAssignmentDetails(courseId, assignmentId);
    }

    @GetMapping("/profile")
    public CanvasUserProfile getProfile() {
        return canvasService.getUserProfile();
    }
}