USE testar;

DROP TABLE IF EXISTS `reports`;
CREATE TABLE `reports` (
    `id` int NOT NULL AUTO_INCREMENT,
    `tag` varchar(255),
    `time` datetime NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `iterations`;
CREATE TABLE `iterations` (
    `id` int NOT NULL AUTO_INCREMENT,
    `report_id` int NOT NULL,
    `info` varchar(255),
    `severity` double,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`report_id`) REFERENCES `reports`(`id`)
);

DROP TABLE IF EXISTS `actions`;
CREATE TABLE `actions` (
    `id` int NOT NULL AUTO_INCREMENT,
    `iteration_id` int NOT NULL,
    `description` varchar(255),
    `name` varchar(255),
    `status` varchar(255),
    `screenshot` varchar(255),
    `start_time` datetime,
    PRIMARY KEY (`id`),
    FOREIGN KEY(`iteration_id`) REFERENCES `iterations` (`id`)
);
