package com.example.studentcrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Student Management System backend.
 * Run this class (or "mvn spring-boot:run") and the REST API starts on http://localhost:8080
 */
@SpringBootApplication
public class StudentCrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentCrudApplication.class, args);
    }
}
