package com.example.homeworkstrateguistsbgradle.canvas.service;

import com.example.homeworkstrateguistsbgradle.canvas.DTO.*;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.*;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CanvasSyncService {

    private final CanvasApiService canvasApiService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final ModuleRepository moduleRepository;
    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;


    public CanvasSyncService(CanvasApiService canvasApiService, UserRepository userRepository,
                             CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                             ModuleRepository moduleRepository,
                             QuizRepository quizRepository,
                             QuizSubmissionRepository quizSubmissionRepository) {
        this.canvasApiService = canvasApiService;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.moduleRepository = moduleRepository;
        this.quizRepository = quizRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
    }

    @Transactional
    public void syncCanvasDataForUser(String googleToken) {
        UserEntity user = userRepository.findByGoogleToken(googleToken)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        List<CanvasCourse> canvasCourses = canvasApiService.getCourses();
        if (canvasCourses == null) return;

        for (CanvasCourse courseDto : canvasCourses) {
            Long courseId = courseDto.getId();
            for (CanvasCourse dto : canvasCourses) {
                // 1. Sync Course
                CourseEntity course = new CourseEntity();
                course.setId(dto.getId());
                course.setUserId(user.getId());
                course.setName(dto.getName());
                course.setCourseCode(dto.getCourseCode());
                course.setTermId(dto.getTermId());
                course.setImageDownloadUrl(dto.getImageDownloadUrl());
                courseRepository.save(course);

                throttle();

                // 2. Sync Assignments
                List<CanvasAssignment> assignments = canvasApiService.getAssignments(dto.getId());
                if (assignments != null) {
                    for (CanvasAssignment aDto : assignments) {
                        AssignmentEntity assign = new AssignmentEntity();
                        assign.setId(aDto.getId());
                        assign.setCourseId(dto.getId());
                        assign.setName(aDto.getName());
                        if (aDto.getDueAt() != null) {
                            assign.setDueAt(aDto.getDueAt().toLocalDateTime());
                        }
                        assign.setDescription(aDto.getDescription());
                        assignmentRepository.save(assign);
                    }
                }
                throttle();

                // 3. sync modules
                List<CanvasModule> modules = canvasApiService.getModulesWithItems(courseId);
                if (modules != null) {
                    for (CanvasModule moduleDto : modules) {
                        ModuleEntity module = new ModuleEntity();
                        module.setId(moduleDto.getId());
                        module.setCourseId(courseId);
                        module.setName(moduleDto.getName());
//                        if (moduleDto.getDescription() != null) {
//                            module.setDescription(moduleDto.getDescription());
//                        }
                        moduleRepository.save(module);
                    }
                }
                throttle();

                // 4. NEW: Sync Quizzes
                List<CanvasQuiz> quizzes = canvasApiService.getQuizzes(courseId);
                if (quizzes != null) {
                    for (CanvasQuiz quizDto : quizzes) {
                        QuizEntity quiz = new QuizEntity();
                        quiz.setId(quizDto.getId());
                        quiz.setCourseId(courseId);
                        quiz.setTitle(quizDto.getTitle());
                        quizRepository.save(quiz);
                    }
                }

//                // 5. NEW: Sync Quiz Submissions (If Entity exists)
//                // NOTE: Quizzes can have hundreds of submissions. Doing this per quiz
//                // might slow down the sync significantly. Use carefully.
//                List<CanvasQuizSubmission> submissions = canvasApiService.getQuizSubmissions(courseId, 0L);
//                // You may need to handle pagination here if Canvas API returns many submissions
//                if (submissions != null) {
//                    for (CanvasQuizSubmission subDto : submissions) {
//                        // Ensure you have a QuizSubmissionEntity defined in your project
//                        // If you haven't created it yet, you only need the logic for Modules/Quizzes first.
//                        // Example:
//                        // QuizSubmissionEntity submission = new QuizSubmissionEntity();
//                        // submission.setQuizId(quizDto.getId()); // Link to quiz
//                        // submission.setUserId(subDto.getUserId()); // Link to user
//                        // submissionRepository.save(submission);
//                    }
//                }
            }
        }
    }

    private void throttle() {
        try { Thread.sleep(250); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}