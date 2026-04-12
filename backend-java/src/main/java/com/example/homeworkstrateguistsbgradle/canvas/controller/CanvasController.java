package com.example.homeworkstrateguistsbgradle.canvas.controller;

import com.example.homeworkstrateguistsbgradle.canvas.DTO.*;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.AssignmentEntity;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.CourseEntity;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.UserEntity;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.repository.AssignmentRepository;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.repository.CourseRepository;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.repository.UserRepository;
import com.example.homeworkstrateguistsbgradle.canvas.service.CanvasApiService;
import com.example.homeworkstrateguistsbgradle.canvas.service.CanvasSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/canvas")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class CanvasController {
    @Autowired
    private CanvasSyncService canvasSyncService;

    private final CanvasApiService canvasService;

    private final UserRepository userRepository;

    public CanvasController(CanvasApiService canvasService,
                            CanvasSyncService canvasSyncService,
                            UserRepository userRepository) { // Add it here
        this.canvasService = canvasService;
        this.canvasSyncService = canvasSyncService;
        this.userRepository = userRepository; // And here
    }

    @PostMapping("/sync")
    public ResponseEntity<String> triggerDatabaseSync(@RequestParam("ivctoken") String ivctoken) {
        try {
            canvasSyncService.syncCanvasDataForUser(ivctoken);
            return ResponseEntity.ok("Successfully synced Canvas data to MySQL container.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Sync Error: " + e.getMessage());
        }
    }

//    @GetMapping("/courses")
//    public List<CanvasCourse> getCourses() {
//        // No headers needed in the request anymore!
//        // The service already has the token from @Value.
//        return canvasService.getCourses();
//    }

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

//    @PostMapping("/token")
//    @CrossOrigin(origins = "http://localhost:4200") // Ensure no trailing slash here
//    public ResponseEntity<String> setToken(@RequestBody String token) {
//        if (token == null || token.isEmpty()) {
//            return ResponseEntity.badRequest().body("Token cannot be empty");
//        }
//        // Update the service's class variable
//        canvasService.setAccessToken(token);
//        return ResponseEntity.ok("Token synced");
//    }

    @PostMapping({"/token"})
    public ResponseEntity<String> saveToken(@RequestBody Map<String, String> payload) {
        String canvasToken = payload.get("token");

        // 1. Update the service IMMEDIATELY so the next API call uses THIS token
        canvasService.setAccessToken(canvasToken);

        // 2. Save to DB for persistence
        UserEntity user = userRepository.findByIvcToken(canvasToken)
                .orElse(new UserEntity());
        user.setIvcToken(canvasToken);
        userRepository.save(user);

        return ResponseEntity.ok("Token saved successfully");
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getLinkStatus(@RequestParam String googleToken) {
        // 1. Fetch user from DB
        return userRepository.findByGoogleToken(googleToken)
                .map(user -> {
                    // 2. Build the response map
                    Map<String, Object> response = new HashMap<>();

                    // Check if the ivcToken (Canvas Token) exists in the DB
                    boolean linked = user.getIvcToken() != null && !user.getIvcToken().isEmpty();

                    response.put("isLinked", linked); // Use .put(), not .add()

                    // 3. If linked, ready the API service immediately
                    if (linked) {
                        canvasService.setAccessToken(user.getIvcToken());
                    }

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    // If user doesn't exist in DB yet, they definitely aren't linked
                    Map<String, Object> response = new HashMap<>();
                    response.put("isLinked", false);
                    return ResponseEntity.ok(response);
                });
    }

    @DeleteMapping("/unlink")
    public ResponseEntity<String> unlinkAccount(@RequestParam String googleToken) {
        userRepository.findByGoogleToken(googleToken).ifPresent(user -> {
            user.setIvcToken(null);
            userRepository.save(user);
        });
        return ResponseEntity.ok("Account unlinked");
    }

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/courses")
    public ResponseEntity<List<CourseEntity>> getAllCourses() {
        List<CourseEntity> courses = courseRepository.findAll();
        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearUserData() {
        try {
            courseRepository.deleteAll();

            // Return a JSON object instead of a plain string
            Map<String, String> response = new HashMap<>();
            response.put("message", "User data cleared successfully.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Autowired
    private AssignmentRepository assignmentRepository;

    @GetMapping("/courses/{courseId}/assignments")
    public ResponseEntity<List<AssignmentEntity>> getDatabaseAssignments(@PathVariable String courseId) {
        Long idAsLong = Long.parseLong(courseId);


        try {
            List<AssignmentEntity> assignments = assignmentRepository.findByCourseId(idAsLong);

            if (assignments.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}