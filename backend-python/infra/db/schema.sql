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
                                                   `id` INT NOT NULL AUTO_INCREMENT,
                                                   `google_token` VARCHAR(512) NOT NULL,
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
CREATE TABLE IF NOT EXISTS `ai_planner`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `google_token` VARCHAR(256) NOT NULL,
  `ivc_token` VARCHAR(256) NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  UNIQUE INDEX `google_token_UNIQUE` (`google_token` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ai_planner`.`status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_planner`.`status` (
  `status` VARCHAR(12) NOT NULL,
  `description` VARCHAR(256) NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`status`),
  INDEX `fk_table1_user_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_table1_user`
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