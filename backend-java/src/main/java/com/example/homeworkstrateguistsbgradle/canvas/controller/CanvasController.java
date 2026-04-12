package com.example.homeworkstrateguistsbgradle.canvas.controller;

import com.example.homeworkstrateguistsbgradle.canvas.DTO.*;
import com.example.homeworkstrateguistsbgradle.canvas.service.CanvasApiService;
import com.example.homeworkstrateguistsbgradle.canvas.service.CanvasSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/canvas")
@CrossOrigin(origins = "http://localhost:4200/")
public class CanvasController {
    @Autowired
    private CanvasSyncService canvasSyncService;

    private final CanvasApiService canvasService;

    public CanvasController(CanvasApiService canvasService, CanvasSyncService canvasSyncService) {
        this.canvasService = canvasService;
        this.canvasSyncService = canvasSyncService;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> triggerDatabaseSync(@RequestParam String googleToken) {
        try {
            canvasSyncService.syncCanvasDataForUser(googleToken);
            return ResponseEntity.ok("Successfully synced Canvas data to MySQL container.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Sync Error: " + e.getMessage());
        }
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

    @GetMapping("/courses/{courseId}/modules")
    public List<CanvasModule> getModules(@PathVariable Long courseId) {
        List<CanvasModule> modules = canvasService.getModulesWithItems(courseId);

        // If modules is null, return an empty list instead of letting it error out
        return modules != null ? modules : List.of();
    }

    @GetMapping("/courses/{courseId}/quizzes")
    public List<CanvasQuiz> getQuizzes(@PathVariable Long courseId) {
        List<CanvasQuiz> quizzes = canvasService.getQuizzes(courseId);
        return quizzes != null ? quizzes : List.of();
    }

    @GetMapping("/courses/{courseId}/quizzes/{quizId}")
    public CanvasQuiz getQuizDetails(@PathVariable Long courseId, @PathVariable Long quizId) {
        return canvasService.getQuizDetails(courseId, quizId);
    }

    @GetMapping("/courses/{courseId}/quizzes/{quizId}/submissions")
    public List<CanvasQuizSubmission> getSubmissions(@PathVariable Long courseId, @PathVariable Long quizId) {
        return canvasService.getQuizSubmissions(courseId, quizId);
    }

    @PostMapping("/token")
    @CrossOrigin(origins = "http://localhost:4200") // Ensure no trailing slash here
    public ResponseEntity<String> setToken(@RequestBody String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Token cannot be empty");
        }
        // Update the service's class variable
        canvasService.setAccessToken(token);
        return ResponseEntity.ok("Token synced");
    }
}