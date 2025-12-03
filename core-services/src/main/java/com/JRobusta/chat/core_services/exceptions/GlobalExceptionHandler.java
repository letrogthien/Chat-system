package com.JRobusta.chat.core_services.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
  private static final String TIMESTAMP = "timestamp";

  @ExceptionHandler({CustomException.class})
  public ResponseEntity<ProblemDetail> handleCustomException(CustomException ex,
      WebRequest request) {
    ErrorCode errorCode = ex.getErrorCode();
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(errorCode.getStatus(),
        ex.getMessage() != null ? ex.getMessage() : errorCode.getMessage());
    problemDetail.setTitle("Authentication Error");
    problemDetail.setProperty("errorCode", errorCode.getCode());
    problemDetail.setProperty(TIMESTAMP, System.currentTimeMillis());
    return new ResponseEntity<>(problemDetail, errorCode.getStatus());
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex, WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred");
    problemDetail.setTitle("Server Error");
    problemDetail.setProperty(TIMESTAMP, System.currentTimeMillis());
    problemDetail.setProperty("errorDetails", "we are sorry for the inconvenience");
    return new ResponseEntity<>(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex,
      WebRequest request) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problemDetail.setTitle("Invalid Argument");
    problemDetail.setProperty(TIMESTAMP, System.currentTimeMillis());
    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<ProblemDetail> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
    problemDetail.setTitle("Validation Error");
    Map<String, String> validationErrors = new HashMap<>();

    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      validationErrors.put(error.getField(), error.getDefaultMessage());
    }

    problemDetail.setProperty("errors", validationErrors);
    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }
}
