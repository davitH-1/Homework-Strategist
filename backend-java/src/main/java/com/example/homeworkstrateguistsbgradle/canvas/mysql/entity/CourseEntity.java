package com.example.homeworkstrateguistsbgradle.canvas.mysql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "courses")
@Getter @Setter
public class CourseEntity {
    @Id
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    private String name;

    @Column(name = "course_code")
    private String courseCode;

    @Column(name = "term_id")
    private Long termId;

    @Column(name = "image_download_url", columnDefinition = "TEXT")
    private String imageDownloadUrl;
}