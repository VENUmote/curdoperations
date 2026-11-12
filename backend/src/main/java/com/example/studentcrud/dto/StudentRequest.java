package com.example.studentcrud.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data the client sends when creating or updating a student.
 * Validation annotations are checked automatically because the controller uses @Valid.
 */
public record StudentRequest(

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email must be at most 150 characters")
        String email,

        @NotBlank(message = "Roll number is required")
        @Pattern(regexp = "^[A-Za-z0-9-]{3,20}$", message = "Roll number: 3-20 letters, digits or '-'")
        String rollNo,

        @NotBlank(message = "Department is required")
        @Size(max = 60, message = "Department must be at most 60 characters")
        String department,

        @NotNull(message = "Year of study is required")
        @Min(value = 1, message = "Year must be between 1 and 4")
        @Max(value = 4, message = "Year must be between 1 and 4")
        Integer yearOfStudy
) {
}
