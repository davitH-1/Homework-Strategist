package com.example.homeworkstrateguistsbgradle.canvas.controller;

import com.example.homeworkstrateguistsbgradle.canvas.DTO.*;
import com.example.homeworkstrateguistsbgradle.canvas.service.CanvasApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/canvas")
@CrossOrigin(origins = "http://localhost:4200/")
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
//    @GetMapping("/profile")
//    public CanvasUserProfile getProfile(@RequestParam("accessToken") String accessToken) {
//        // This ensures 'accessToken' in the URL is mapped to this variable
//        return canvasService.getUserProfile(accessToken);
//    }

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

//    @GetMapping("/profile")
//    public CanvasUserProfile getProfile(@RequestParam(required = false) String accessToken) {
//        // 1. If no token is provided by Angular, use the one already in the service
//        if (accessToken == null || accessToken.isEmpty()) {
//            return canvasService.getUserProfile();
//        }
//
//        // 2. If a token IS provided, verify it first
//        CanvasUserProfile profile = canvasService.getUserProfile();
//
//        // 3. If the profile was found (token is good), "Sync" it to the class variable
//        if (profile != null) {
//            canvasService.setAccessToken(accessToken);
//        }
//
//        return profile;
//    }
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