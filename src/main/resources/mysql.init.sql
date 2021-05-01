CREATE TABLE westworld.farmland (
	fire_time BIGINT NOT NULL,
	task varchar(500) DEFAULT "" NOT NULL,
	status TINYINT DEFAULT 0 NOT NULL,
	CONSTRAINT farmland_PK PRIMARY KEY (fire_time)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;