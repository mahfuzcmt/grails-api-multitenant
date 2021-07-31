-- Add device_mac column
ALTER TABLE user ADD `device_mac` VARCHAR(255) NULL DEFAULT NULL;

-- Add operation log table

CREATE TABLE `operation_log` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`version` BIGINT(20) NOT NULL,
	`created` DATETIME NOT NULL,
	`device_mac` VARCHAR(255) NULL DEFAULT NULL,
	`address` VARCHAR(255) NULL DEFAULT NULL,
	`user_id` BIGINT(20) NULL DEFAULT NULL,
	`current_balance` DOUBLE NOT NULL,
	`operation_type` VARCHAR(255) NOT NULL,
	`longitude` VARCHAR(255) NOT NULL,
	`latitude` VARCHAR(255) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FKbu047c0s7dv7e1dtpvuedch19` (`user_id`),
	CONSTRAINT `FKbu047c0s7dv7e1dtpvuedch19` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=77
;
