CREATE DATABASE IF NOT EXISTS `shaba_members`;

USE `shaba_members`;

CREATE TABLE `user` (
    user_id int NOT NULL AUTO_INCREMENT PRIMARY KEY
  , username VARCHAR(30) NOT NULL
  , password VARCHAR(60) NOT NULL
  , enabled BOOLEAN NOT NULL
  , first_name VARCHAR(45) NOT NULL
  , last_name VARCHAR(45) NOT NULL
  , email VARCHAR(60) NOT NULL
  , UNIQUE(username)
  , UNIQUE(email)
)
;

CREATE TABLE `user_role` (
    user_role_id int NOT NULL AUTO_INCREMENT PRIMARY KEY
  , username VARCHAR(30) NOT NULL
  , `role` VARCHAR(60) NOT NULL
  , FOREIGN KEY (username) REFERENCES `user`(username)
  , UNIQUE(username, `role`)
)
;

