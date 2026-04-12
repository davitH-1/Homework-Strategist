package com.example.homeworkstrateguistsbgradle.canvas.service;

import com.example.homeworkstrateguistsbgradle.canvas.DTO.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanForwardService {

    private static final double DEFAULT_HOURS_PER_ASSIGNMENT = 2.0;

    private final CanvasApiService canvasApiService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${python.backend.url:http://localhost:8000}")
    private String pythonBackendUrl;

    public PlanForwardService(CanvasApiService canvasApiService) {
        this.canvasApiService = canvasApiService;
    }

    /**
     * Fetches all Canvas assignments across all active courses, builds a
     * PlanRequest, forwards it to the Python backend, and returns the response.
     */
    public PlanResponseDTO generatePlan(String userId) {
        List<PlanClassItemDTO> items = buildClassItems();

        if (items.isEmpty()) {
            PlanResponseDTO empty = new PlanResponseDTO();
            empty.setUserId(userId);
            empty.setMessage("No upcoming assignments with due dates were found in Canvas.");
            empty.setScheduledEvents(List.of());
            return empty;
        }

        PlanRequestDTO request = new PlanRequestDTO(userId, items);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PlanRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<PlanResponseDTO> response = restTemplate.exchange(
                pythonBackendUrl + "/plan",
                HttpMethod.POST,
                entity,
                PlanResponseDTO.class
        );

        return response.getBody();
    }

    private List<PlanClassItemDTO> buildClassItems() {
        List<CanvasCourse> courses = canvasApiService.getCourses();
        if (courses == null) return List.of();

        List<PlanClassItemDTO> items = new ArrayList<>();
        for (CanvasCourse course : courses) {
            List<CanvasAssignment> assignments = canvasApiService.getAssignments(course.getId());
            if (assignments == null) continue;

            for (CanvasAssignment assignment : assignments) {
                // Skip assignments without a due date — the planner needs one
                if (assignment.getDueAt() == null) continue;

                String dueDate = assignment.getDueAt().toLocalDate().toString(); // YYYY-MM-DD
                items.add(new PlanClassItemDTO(
                        assignment.getName(),
                        dueDate,
                        DEFAULT_HOURS_PER_ASSIGNMENT
                ));
            }
        }
        return items;
    }
}