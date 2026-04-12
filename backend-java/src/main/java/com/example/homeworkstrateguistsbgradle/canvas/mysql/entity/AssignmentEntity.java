package com.example.homeworkstrateguistsbgradle.canvas.mysql.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Getter @Setter
public class AssignmentEntity {
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @Column(name = "course_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long courseId;

    private String name;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

}