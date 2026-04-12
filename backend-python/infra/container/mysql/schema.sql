-- MySQL Workbench Forward Engineering
-- Schema ai_planner

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

CREATE SCHEMA IF NOT EXISTS `ai_planner` DEFAULT CHARACTER SET utf8 ;
USE `ai_planner` ;

-- -----------------------------------------------------
-- Table `ai_planner`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_planner`.`user` (
                                                   `id` BIGINT NOT NULL AUTO_INCREMENT,
                                                   `google_token` VARCHAR(512) NULL,
    `ivc_token` VARCHAR(512) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status_active` TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `google_token_UNIQUE` (`google_token` ASC) VISIBLE)
    ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `ai_planner`.`courses` (Maps to CanvasCourse.java)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_planner`.`courses` (
                                                      `id` BIGINT NOT NULL,
                                                      `user_id` INT NOT NULL,
                                                      `name` VARCHAR(255) NULL,
    `course_code` VARCHAR(100) NULL,
    `term_id` BIGINT NULL,
    `image_download_url` TEXT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_courses_user_idx` (`user_id` ASC) VISIBLE,
    CONSTRAINT `fk_courses_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `ai_planner`.`user` (`id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `ai_planner`.`assignments` (Maps to CanvasAssignment.java)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_planner`.`assignments` (
                                                          `id` BIGINT NOT NULL,
                                                          `course_id` BIGINT NOT NULL,
                                                          `name` VARCHAR(255) NULL,
    `due_at` DATETIME NULL,
    `description` LONGTEXT NULL, -- Uses LONGTEXT for HTML descriptions
    PRIMARY KEY (`id`),
    INDEX `fk_assignments_courses_idx` (`course_id` ASC) VISIBLE,
    CONSTRAINT `fk_assignments_courses`
    FOREIGN KEY (`course_id`)
    REFERENCES `ai_planner`.`courses` (`id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `ai_planner`.`quizzes` (Maps to CanvasQuiz.java)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_planner`.`quizzes` (
                                                      `id` BIGINT NOT NULL,
                                                      `course_id` BIGINT NOT NULL,
                                                      `title` VARCHAR(255) NULL,
    `html_url` TEXT NULL,
    `quiz_type` VARCHAR(50) NULL,
    `time_limit` INT NULL,
    `allowed_attempts` INT NULL,
    `due_at` DATETIME NULL,
    `published` TINYINT(1) NULL,
    `question_count` INT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_quizzes_courses_idx` (`course_id` ASC) VISIBLE,
    CONSTRAINT `fk_quizzes_courses`
    FOREIGN KEY (`course_id`)
    REFERENCES `ai_planner`.`courses` (`id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `ai_planner`.`quiz_submissions` (Maps to CanvasQuizSubmission.java)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_planner`.`quiz_submissions` (
                                                               `id` BIGINT NOT NULL,
                                                               `quiz_id` BIGINT NOT NULL,
                                                               `user_id` INT NOT NULL,
                                                               `attempt` INT NULL,
                                                               `score` DOUBLE NULL,
                                                               `time_spent` INT NULL, -- Stored in seconds
                                                               `finished_at` DATETIME NULL,
                                                               PRIMARY KEY (`id`),
    INDEX `fk_submissions_quizzes_idx` (`quiz_id` ASC) VISIBLE,
    INDEX `fk_submissions_user_idx` (`user_id` ASC) VISIBLE,
    CONSTRAINT `fk_submissions_quizzes`
    FOREIGN KEY (`quiz_id`)
    REFERENCES `ai_planner`.`quizzes` (`id`)
    ON DELETE CASCADE,
    CONSTRAINT `fk_submissions_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `ai_planner`.`user` (`id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `ai_planner`.`modules` (Maps to CanvasModule.java)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_planner`.`modules` (
                                                      `id` BIGINT NOT NULL,
                                                      `course_id` BIGINT NOT NULL,
                                                      `name` VARCHAR(255) NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_modules_courses_idx` (`course_id` ASC) VISIBLE,
    CONSTRAINT `fk_modules_courses`
    FOREIGN KEY (`course_id`)
    REFERENCES `ai_planner`.`courses` (`id`)
    ON DELETE CASCADE)
    ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;