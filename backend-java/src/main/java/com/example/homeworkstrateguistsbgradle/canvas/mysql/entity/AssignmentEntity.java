package com.example.homeworkstrateguistsbgradle.canvas.mysql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Getter @Setter
public class AssignmentEntity {
    @Id
    private Long id;

    @Column(name = "course_id")
    private Long courseId;

    private String name;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(columnDefinition = "LONGTEXT")
    private String description;
    
}