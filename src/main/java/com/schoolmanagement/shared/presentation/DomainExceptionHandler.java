package com.schoolmanagement.shared.presentation;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.schoolmanagement.course.domain.exception.CourseNotFound;
import com.schoolmanagement.iam.domain.exception.InvalidCredentials;
import com.schoolmanagement.iam.domain.exception.UsernameAlreadyTaken;
import com.schoolmanagement.promotion.domain.exception.PromotionFull;
import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;
import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.student.domain.exception.StudentAlreadyArchived;
import com.schoolmanagement.student.domain.exception.StudentAlreadyAssignedToPromotion;
import com.schoolmanagement.student.domain.exception.StudentNotFound;
import com.schoolmanagement.teacher.domain.exception.TeacherAlreadyArchived;
import com.schoolmanagement.teacher.domain.exception.TeacherNotFound;

@RestControllerAdvice
public class DomainExceptionHandler {

  @ExceptionHandler(StudentNotFound.class)
  public ResponseEntity<ApiError> notFound(StudentNotFound ex) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(PromotionNotFound.class)
  public ResponseEntity<ApiError> notFound(PromotionNotFound ex) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(TeacherNotFound.class)
  public ResponseEntity<ApiError> notFound(TeacherNotFound ex) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(CourseNotFound.class)
  public ResponseEntity<ApiError> notFound(CourseNotFound ex) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(InvalidCredentials.class)
  public ResponseEntity<ApiError> unauthorized(InvalidCredentials ex) {
    return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  @ExceptionHandler(StudentAlreadyArchived.class)
  public ResponseEntity<ApiError> conflict(StudentAlreadyArchived ex) {
    return build(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(TeacherAlreadyArchived.class)
  public ResponseEntity<ApiError> conflict(TeacherAlreadyArchived ex) {
    return build(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(PromotionFull.class)
  public ResponseEntity<ApiError> conflict(PromotionFull ex) {
    return build(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(StudentAlreadyAssignedToPromotion.class)
  public ResponseEntity<ApiError> conflict(StudentAlreadyAssignedToPromotion ex) {
    return build(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(UsernameAlreadyTaken.class)
  public ResponseEntity<ApiError> conflict(UsernameAlreadyTaken ex) {
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
