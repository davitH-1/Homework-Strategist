package com.example.homeworkstrateguistsbgradle.canvas.mysql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "modules")
public class ModuleEntity {
    @Id private Long id;
    @Column(name = "course_id") private Long courseId;
    private String name;

    // Getters and Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getCourseId() { return courseId; } public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
}
