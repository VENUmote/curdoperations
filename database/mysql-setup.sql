-- OPTIONAL: only needed if you use the MySQL profile.
-- Spring Boot creates the table automatically (spring.jpa.hibernate.ddl-auto=update),
-- so this file is for reference / manual setup.

CREATE DATABASE IF NOT EXISTS studentdb;
USE studentdb;

CREATE TABLE IF NOT EXISTS students (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    roll_no       VARCHAR(20)  NOT NULL UNIQUE,
    department    VARCHAR(60)  NOT NULL,
    year_of_study INT          NOT NULL,
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL
);

-- Handy queries
-- SELECT * FROM students;
-- SELECT department, COUNT(*) FROM students GROUP BY department;
