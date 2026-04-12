package com.example.homeworkstrateguistsbgradle.canvas.service;

import com.example.homeworkstrateguistsbgradle.canvas.DTO.*;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.AssignmentEntity;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.CourseEntity;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.UserEntity;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.repository.AssignmentRepository;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.repository.CourseRepository;
import com.example.homeworkstrateguistsbgradle.canvas.mysql.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CanvasSyncService {

    private final CanvasApiService canvasApiService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;

    public CanvasSyncService(CanvasApiService canvasApiService, UserRepository userRepository,
                             CourseRepository courseRepository, AssignmentRepository assignmentRepository) {
        this.canvasApiService = canvasApiService;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional
    public void syncCanvasDataForUser(String googleToken) {
        UserEntity user = userRepository.findByGoogleToken(googleToken)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        List<CanvasCourse> canvasCourses = canvasApiService.getCourses();
        if (canvasCourses == null) return;

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
        }
    }

    private void throttle() {
        try { Thread.sleep(250); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}