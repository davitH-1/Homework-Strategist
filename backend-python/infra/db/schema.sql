-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema ai_planner
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema ai_planner
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `ai_planner` DEFAULT CHARACTER SET utf8 ;
USE `ai_planner` ;

-- -----------------------------------------------------
-- Table `ai_planner`.`user`
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
  UNIQUE INDEX `google_tiken_UNIQUE` (`google_token` ASC) VISIBLE)
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
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
