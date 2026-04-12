package com.example.homeworkstrateguistsbgradle.canvas.mysql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "quizzes")
public class QuizEntity {
    @Id private Long id;
    @Column(name = "course_id") private Long courseId;
    private String title;
    @Column(name = "html_url") private String htmlUrl;
    @Column(name = "question_count") private Integer questionCount;

    // Getters and Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getCourseId() { return courseId; } public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
}
