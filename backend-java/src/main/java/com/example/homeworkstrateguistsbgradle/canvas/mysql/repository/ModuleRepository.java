package com.example.homeworkstrateguistsbgradle.canvas.mysql.repository;

import com.example.homeworkstrateguistsbgradle.canvas.mysql.entity.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {}
