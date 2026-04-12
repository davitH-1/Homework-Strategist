package com.example.homeworkstrateguistsbgradle.canvas.mysql.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("course_code")
    private String courseCode;

    @Column(name = "term_id")
    @JsonProperty("term_id")
    private Long termId;

    @Column(name = "image_download_url")
    @JsonProperty("image_download_url") // This ensures the JSON sent to Angular has the underscores
    private String imageDownloadUrl;
}