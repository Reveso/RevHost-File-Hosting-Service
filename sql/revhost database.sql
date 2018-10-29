CREATE DATABASE  IF NOT EXISTS `revhost_database`;
USE `revhost_database`;

DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `authorities`;
DROP TABLE IF EXISTS `files`;
DROP TABLE IF EXISTS `public-files`;
DROP TABLE IF EXISTS `UserConnection`;

CREATE TABLE `users` (
	`username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(100) NOT NULL,
    `enabled` TINYINT(1) NOT NULL,
    PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `authorities` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `authority` VARCHAR(50) NOT NULL,
    UNIQUE KEY `authorities_idx_1` (`username`,`authority`),
    CONSTRAINT `authorities_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `files` (
	`file_code` varchar(45) NOT NULL,
    `bucket_name` varchar(120) NOT NULL,
    `file_key` varchar(120) NOT NULL,
	`file_name` varchar(120) NOT NULL,
    `file_size` bigint NOT NULL,
    `addition_date` datetime DEFAULT NULL,
    `public_access` boolean NOT NULL,
	`username` varchar(50) NOT NULL,
    `url` VARCHAR(2083),
	FOREIGN KEY (`username`) REFERENCES `users` (`username`),
    PRIMARY KEY(`file_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS UserConnection;
create table UserConnection (
	userId varchar(255) not null,
	providerId varchar(255) not null,
	providerUserId varchar(255),
	rank int not null,
	displayName varchar(255),
	profileUrl varchar(512),
	imageUrl varchar(512),
	accessToken varchar(512) not null,
	secret varchar(512),
	refreshToken varchar(512),
	expireTime bigint,
	PRIMARY KEY (userId, providerId, providerUserId)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE UNIQUE INDEX UserConnectionRank ON UserConnection (userId, providerId, rank);


UNLOCK TABLES;