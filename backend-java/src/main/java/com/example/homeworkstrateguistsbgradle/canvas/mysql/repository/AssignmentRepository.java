package com.example.homeworkstrateguistsbgradle.canvas.mysql.repository;

import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.AssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentEntity, Long> {
    // Spring Data JPA derives the query: SELECT * FROM assignments WHERE course_id = ?
    List<AssignmentEntity> findByCourseId(Long courseId);
}