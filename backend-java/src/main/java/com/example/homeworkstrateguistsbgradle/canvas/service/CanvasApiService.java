package com.example.homeworkstrateguistsbgradle.canvas.service;

import com.example.homeworkstrateguistsbgradle.canvas.DTO.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class CanvasApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${canvas.api.token}")
    private String accessToken;

    @Value("${canvas.api.domain}")
    private String baseDomain;

    public List<CanvasCourse> getCourses() {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://" + baseDomain + "/api/v1/courses")
                .queryParam("enrollment_state", "active")
                .queryParam("per_page", "100")
                .build().toUri();

        return callCanvasApi(uri, new ParameterizedTypeReference<List<CanvasCourse>>() {});
    }

    public List<CanvasAssignment> getAssignments(Long courseId) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://" + baseDomain + "/api/v1/courses/" + courseId + "/assignments")
                .queryParam("per_page", "100")
                .build().toUri();

        return callCanvasApi(uri, new ParameterizedTypeReference<List<CanvasAssignment>>() {});
    }

    public CanvasAssignment getAssignmentDetails(Long courseId, Long assignmentId) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://" + baseDomain + "/api/v1/courses/" + courseId + "/assignments/" + assignmentId)
                .build().toUri();

        return callCanvasApi(uri, new ParameterizedTypeReference<CanvasAssignment>() {});
    }

    public CanvasUserProfile getUserProfile() {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://" + baseDomain + "/api/v1/users/self/profile")
                .build().toUri();

        return callCanvasApi(uri, new ParameterizedTypeReference<CanvasUserProfile>() {});
    }

    public List<CanvasModule> getModulesWithItems(Long courseId) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://" + baseDomain + "/api/v1/courses/" + courseId + "/modules")
                .queryParam("include[]", "items")
                .queryParam("per_page", "50")
                .build().toUri();

        return callCanvasApi(uri, new ParameterizedTypeReference<List<CanvasModule>>() {});
    }

    public List<CanvasQuiz> getQuizzes(Long courseId) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://" + baseDomain + "/api/v1/courses/" + courseId + "/quizzes")
                .queryParam("per_page", "100")
                .build().toUri();

        return callCanvasApi(uri, new ParameterizedTypeReference<List<CanvasQuiz>>() {});
    }

    public CanvasQuiz getQuizDetails(Long courseId, Long quizId) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://" + baseDomain + "/api/v1/courses/" + courseId + "/quizzes/" + quizId)
                .build().toUri();

        return callCanvasApi(uri, new ParameterizedTypeReference<CanvasQuiz>() {});
    }

    public List<CanvasQuizSubmission> getQuizSubmissions(Long courseId, Long quizId) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://" + baseDomain + "/api/v1/courses/" + courseId + "/quizzes/" + quizId + "/submissions")
                .build().toUri();

        QuizSubmissionWrapper wrapper = callCanvasApi(uri, new ParameterizedTypeReference<QuizSubmissionWrapper>() {});

        return (wrapper != null && wrapper.getSubmissions() != null)
                ? wrapper.getSubmissions()
                : List.of();
    }

    private <T> T callCanvasApi(URI uri, ParameterizedTypeReference<T> responseType) {
        if (accessToken == null || accessToken.isEmpty()) {
            System.err.println("ERROR: No Canvas Access Token found!");
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.trim());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(uri, HttpMethod.GET, entity, responseType);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("API ERROR: " + e.getMessage());
            return null;
        }
    }
}