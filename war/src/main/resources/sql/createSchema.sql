CREATE TABLE IF NOT EXISTS `kjm`.`prayers` (
  `first_name` VARCHAR(32) NOT NULL,
  `last_name` VARCHAR(32) NOT NULL,
  `county` VARCHAR(16) NOT NULL,
  `date` DATE NOT NULL,
  `prayer` VARCHAR(512) NULL,
  PRIMARY KEY (`first_name`, `last_name`, `county`, `date`));

CREATE TABLE IF NOT EXISTS `kjm`.`inmates` (
  `first_name` VARCHAR(32) NOT NULL,
  `last_name` VARCHAR(32) NOT NULL,
  `county` VARCHAR(16) NOT NULL,
  `release_date` DATE NULL,
  `sex` VARCHAR(1) NULL,
  `info` VARCHAR(256) NULL,
  PRIMARY KEY (`first_name`, `last_name`, `county`));
  
CREATE TABLE IF NOT EXISTS `kjm`.`jails` (
  `county` VARCHAR(16) NOT NULL,
  PRIMARY KEY (`county`));

ALTER TABLE `kjm`.`prayers` 
ADD CONSTRAINT `fk_name`
  FOREIGN KEY (`first_name` , `last_name`, `county`)
  REFERENCES `kjm`.`inmates` (`first_name` , `last_name`, `county`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

ALTER TABLE `kjm`.`inmates` 
ADD INDEX `fk_county_idx` (`county` ASC);
;
ALTER TABLE `kjm`.`inmates` 
ADD CONSTRAINT `fk_county`
  FOREIGN KEY (`county`)
  REFERENCES `kjm`.`jails` (`county`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

CREATE TABLE IF NOT EXISTS `kjm`.`users` (
  `email` VARCHAR(64) NOT NULL,
  `code` VARCHAR(64) NULL,
  `code_expires` DATE NULL,
  `can_login` INT NOT NULL DEFAULT 0,
  `can_edit_inmates` INT NOT NULL DEFAULT 0,
  `can_edit_users` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`email`));
