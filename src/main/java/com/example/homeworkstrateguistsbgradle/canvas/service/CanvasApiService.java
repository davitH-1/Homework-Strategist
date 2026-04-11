package com.example.homeworkstrateguistsbgradle.canvas.service;

import com.example.homeworkstrateguistsbgradle.canvas.DTO.CanvasAssignment;
import com.example.homeworkstrateguistsbgradle.canvas.DTO.CanvasCourse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class CanvasApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${canvas.api.token}")
    private String accessToken;

    @Value("${canvas.api.domain}")
    private String baseDomain;

    public List<CanvasCourse> getCourses() {
        String url = UriComponentsBuilder.fromHttpUrl("https://" + baseDomain + "/api/v1/courses")
                .queryParam("enrollment_state", "active")
                .queryParam("per_page", "100")
                .toUriString();

        // No need to pass token in; it's already a class member
        return callCanvasApi(url, new ParameterizedTypeReference<List<CanvasCourse>>() {});
    }

    public List<CanvasAssignment> getAssignments(Long courseId) {
        String url = UriComponentsBuilder.fromHttpUrl("https://" + baseDomain + "/api/v1/courses/" + courseId + "/assignments")
                .queryParam("per_page", "100")
                .toUriString();

        return callCanvasApi(url, new ParameterizedTypeReference<List<CanvasAssignment>>() {});
    }

    public CanvasAssignment getAssignmentDetails(Long courseId, Long assignmentId) {
        String url = UriComponentsBuilder.fromHttpUrl("https://" + baseDomain + "/api/v1/courses/" + courseId + "/assignments/" + assignmentId)
                .toUriString();

        return callCanvasApi(url, new ParameterizedTypeReference<CanvasAssignment>() {});
    }

    private <T> T callCanvasApi(String url, ParameterizedTypeReference<T> responseType) {
        if (accessToken == null || accessToken.isEmpty()) {
            System.err.println("ERROR: No Canvas Access Token found in environment variables!");
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.trim());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("API ERROR: " + e.getMessage());
            return null;
        }
    }
}