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
    public void syncCanvasDataForUser(String ivctoken) {
        // CRITICAL: Force the API service to use this specific token.
        // This prevents the system from defaulting to hardcoded tokens in application.properties.
        canvasApiService.setAccessToken(ivctoken);

        UserEntity user = userRepository.findByIvcToken(ivctoken)
                .orElseThrow(() -> new RuntimeException("User not found in DB with provided token"));

        List<CanvasCourse> canvasCourses = canvasApiService.getCourses();
        if (canvasCourses == null) return;

        for (CanvasCourse courseDto : canvasCourses) {
            Long courseId = courseDto.getId();

            // 1. Sync Course
            CourseEntity course = new CourseEntity();
            course.setId(courseId);
            course.setUserId(user.getId());
            course.setName(courseDto.getName());
            course.setCourseCode(courseDto.getCourseCode());
            course.setTermId(courseDto.getTermId());
            course.setImageDownloadUrl(courseDto.getImageDownloadUrl());
            courseRepository.save(course);

            throttle();

            // 2. Sync Assignments & Quiz Pivot
            List<CanvasAssignment> assignments = canvasApiService.getAssignments(courseId);
            if (assignments != null) {
                for (CanvasAssignment aDto : assignments) {
                    AssignmentEntity assign = new AssignmentEntity();
                    assign.setId(aDto.getId());
                    assign.setCourseId(courseId);
                    assign.setName(aDto.getName());
                    assign.setDescription(aDto.getDescription());
                    if (aDto.getDueAt() != null) {
                        assign.setDueAt(aDto.getDueAt().toLocalDateTime());
                    }
                    assignmentRepository.save(assign);

                    // If this assignment is a quiz, sync details to QuizEntity immediately
                    if (aDto.getQuizId() != null) {
                        CanvasQuiz fullQuiz = canvasApiService.getQuizDetails(courseId, aDto.getQuizId());
                        if (fullQuiz != null) {
                            saveQuiz(fullQuiz, courseId);
                        }
                    }
                }
            }

            throttle();

            // 3. Sync Modules
            List<CanvasModule> modules = canvasApiService.getModulesWithItems(courseId);
            if (modules != null) {
                for (CanvasModule moduleDto : modules) {
                    ModuleEntity module = new ModuleEntity();
                    module.setId(moduleDto.getId());
                    module.setCourseId(courseId);
                    module.setName(moduleDto.getName());
                    moduleRepository.save(module);
                }
            }

            throttle();

            // 4. Sync Standalone Quizzes (to catch quizzes not linked to assignments)
            List<CanvasQuiz> quizzes = canvasApiService.getQuizzes(courseId);
            if (quizzes != null) {
                for (CanvasQuiz qDto : quizzes) {
                    CanvasQuiz fullQuiz = canvasApiService.getQuizDetails(courseId, qDto.getId());
                    if (fullQuiz != null) {
                        saveQuiz(fullQuiz, courseId);
                    }
                    try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            }

            // 5. Sync Quiz Submissions
            List<CanvasQuizSubmission> submissions = canvasApiService.getQuizSubmissions(courseId, null);
            if (submissions != null) {
                for (CanvasQuizSubmission subDto : submissions) {
                    QuizSubmissionEntity subEntity = new QuizSubmissionEntity();
                    subEntity.setId(subDto.getId());
                    subEntity.setQuizId(subDto.getQuizId());
                    subEntity.setUserId(subDto.getUserId());
                    subEntity.setAttempt(subDto.getAttempt());
                    subEntity.setScore(subDto.getScore());
                    subEntity.setTimeSpent(subDto.getTimeSpent());

                    if (subDto.getFinishedAt() != null) {
                        subEntity.setFinishedAt(subDto.getFinishedAt().toLocalDateTime());
                    }
                    quizSubmissionRepository.save(subEntity);
                }
            }
            throttle();
        }
    }

    /**
     * Helper to prevent duplicate logic for saving Quiz details
     */
    private void saveQuiz(CanvasQuiz fullQuiz, Long courseId) {
        QuizEntity quiz = new QuizEntity();
        quiz.setId(fullQuiz.getId());
        quiz.setCourseId(courseId);
        quiz.setTitle(fullQuiz.getTitle());
        quiz.setDescription(fullQuiz.getDescription());
        quiz.setTimeLimit(fullQuiz.getTimeLimit());
        quiz.setQuestionCount(fullQuiz.getQuestionCount());
        if (fullQuiz.getDueAt() != null) {
            quiz.setDueAt(fullQuiz.getDueAt().toLocalDateTime());
        }
        quizRepository.save(quiz);
    }

    private void throttle() {
        try { Thread.sleep(250); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}