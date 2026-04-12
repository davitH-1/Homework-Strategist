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
        UserEntity user = userRepository.findByIvcToken(ivctoken)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        List<CanvasCourse> canvasCourses = canvasApiService.getCourses();
        if (canvasCourses == null) return;

        // JUST ONE LOOP
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

            // 2. Sync Assignments & Pivot for Quizzes
            List<CanvasAssignment> assignments = canvasApiService.getAssignments(courseId);
            if (assignments != null) {
                for (CanvasAssignment aDto : assignments) {
                    // Save the Assignment as usual
                    AssignmentEntity assign = new AssignmentEntity();
                    assign.setId(aDto.getId());
                    assign.setCourseId(courseId);
                    assign.setName(aDto.getName());
                    assign.setDescription(aDto.getDescription()); // Assignments have their own description
                    if (aDto.getDueAt() != null) {
                        assign.setDueAt(aDto.getDueAt().toLocalDateTime());
                    }
                    assignmentRepository.save(assign);

                    // PIVOT: If this assignment is actually a quiz, get the "Deep" details
                    if (aDto.getQuizId() != null) {
                        CanvasQuiz fullQuiz = canvasApiService.getQuizDetails(courseId, aDto.getQuizId());

                        if (fullQuiz != null) {
                            QuizEntity quiz = new QuizEntity();
                            quiz.setId(fullQuiz.getId());
                            quiz.setCourseId(courseId);
                            quiz.setTitle(fullQuiz.getTitle());
                            quiz.setDescription(fullQuiz.getDescription());
                            quiz.setTimeLimit(fullQuiz.getTimeLimit());
                            quiz.setQuestionCount(fullQuiz.getQuestionCount());

                            // ADD THIS: Map the Due Date
                            if (fullQuiz.getDueAt() != null) {
                                // Re-use the logic you used for Assignments
                                quiz.setDueAt(fullQuiz.getDueAt().toLocalDateTime());
                            }

                            quizRepository.save(quiz);
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

            // 4. Sync Quizzes (Deep Sync for full metadata)
            List<CanvasQuiz> quizzes = canvasApiService.getQuizzes(courseId);

            if (quizzes != null) {
                System.out.println("DEBUG: Course " + courseId + " - Found " + quizzes.size() + " quizzes.");

                for (CanvasQuiz qDto : quizzes) {
                    // CALL THE API AGAIN: Get full details for THIS specific quiz
                    // This ensures fields like description, time_limit, and question_count are populated
                    CanvasQuiz fullQuiz = canvasApiService.getQuizDetails(courseId, qDto.getId());

                    if (fullQuiz != null) {
                        System.out.println("DEBUG: Deep Syncing Quiz ID: " + fullQuiz.getId() + " | Title: " + fullQuiz.getTitle());

                        QuizEntity quiz = new QuizEntity();
                        quiz.setId(fullQuiz.getId());
                        quiz.setCourseId(courseId);
                        quiz.setTitle(fullQuiz.getTitle());

                        // These fields usually require the 'getQuizDetails' call
                        quiz.setDescription(fullQuiz.getDescription());
                        quiz.setTimeLimit(fullQuiz.getTimeLimit());
                        quiz.setQuestionCount(fullQuiz.getQuestionCount());

                        quizRepository.save(quiz);
                    }

                    // Short throttle to avoid hitting the rate limit since we are making N extra calls
                    try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            }

            // 5. Sync Quiz Submissions
// Fetching all submissions for the course is often more efficient than per-quiz
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

    private void throttle() {
        try { Thread.sleep(250); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}