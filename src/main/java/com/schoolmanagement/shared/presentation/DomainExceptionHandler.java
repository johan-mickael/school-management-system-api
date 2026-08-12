package com.schoolmanagement.shared.presentation;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.student.domain.exception.StudentAlreadyArchived;
import com.schoolmanagement.student.domain.exception.StudentNotFound;

@RestControllerAdvice
public class DomainExceptionHandler {

  @ExceptionHandler(StudentNotFound.class)
  public ResponseEntity<ApiError> notFound(StudentNotFound ex) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(StudentAlreadyArchived.class)
  public ResponseEntity<ApiError> conflict(StudentAlreadyArchived ex) {
    return build(HttpStatus.CONFLICT, ex.getMessage());
  }

  // catch-all for invalid value objects
  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ApiError> domain(DomainException ex) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  // edge validation failures -> 400 with per-field messages
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
    Map<String, String> fields = new HashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(f -> fields.put(f.getField(), f.getDefaultMessage()));
    ApiError body = new ApiError(
        Instant.now(),
        400,
        "Bad Request",
        "Validation failed",
        fields);
    return ResponseEntity.badRequest().body(body);
  }

  private ResponseEntity<ApiError> build(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(
        new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, null));
  }
}
