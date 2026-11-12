package com.example.studentcrud.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * The JSON body returned whenever a request fails, e.g.
 * { "status": 400, "error": "Bad Request", "message": "Validation failed",
 *   "fieldErrors": { "email": "Email must be valid" } }
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors
) {
}
