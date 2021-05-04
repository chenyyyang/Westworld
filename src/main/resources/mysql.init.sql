-- westworld.farmland definition

CREATE TABLE `farmland` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fire_time` bigint NOT NULL,
  `task` varchar(500) NOT NULL DEFAULT '',
  `status` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `farmland_fire_time_IDX` (`fire_time`,`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;