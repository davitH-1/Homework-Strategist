package com.example.homeworkstrateguistsbgradle.canvas.controller;

import com.example.homeworkstrateguistsbgradle.canvas.DTO.*;
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
}