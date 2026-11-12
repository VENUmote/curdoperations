package com.example.studentcrud.exception;

public class DuplicateStudentException extends RuntimeException {

    private final String field;

    public DuplicateStudentException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
