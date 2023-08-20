package com.example.configurations;



import com.example.exceptions.BusinessException;
import com.example.exceptions.ExceptionResolver;
import com.example.exceptions.NotFoundException;
import com.example.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.MDC;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlerConfig {

    @ExceptionHandler(value = {BusinessException.class})
    protected ResponseEntity<Object> handleConflict(BusinessException e, HttpServletRequest request) {
        return getException(e.getHttpStatusCode(), e.getMessage(), e.getDescription(), request, "BusinessException");
    }

    @ExceptionHandler({BindException.class})
    public ResponseEntity<Object> handleException(BindException e, HttpServletRequest request) {
        String errors = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).filter(Objects::nonNull).map(String::new)
                .collect(Collectors.joining());
        return getException(HttpStatus.BAD_REQUEST, Constants.CONSTRAINT_VALIDATION_FAILED, errors, request, "BindException");
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        List<String> errors = e.getConstraintViolations().stream()
                .map(violation -> violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage()).toList();
        return getException(HttpStatus.BAD_REQUEST, Constants.CONSTRAINT_VALIDATION_FAILED, errors.toString(), request, "ConstraintViolationException");
    }

    @ExceptionHandler({ClientAbortException.class})
    public ResponseEntity<Object> handleException(ClientAbortException e, HttpServletRequest request) {
        return getException(HttpStatus.valueOf(499), ofNullable(e.getMessage()).orElse(e.toString()), ExceptionResolver.getRootException(e), request, "ClientAbortException");
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    public ResponseEntity<Object> handleException(EmptyResultDataAccessException e, HttpServletRequest request) {
        return getException(HttpStatus.NOT_FOUND, Constants.NOT_FOUND, ExceptionResolver.getRootException(e), request, "EmptyResultDataAccessException");
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> handleException(NotFoundException e, HttpServletRequest request) {
        return getException(HttpStatus.NOT_FOUND, Constants.NOT_FOUND, ExceptionResolver.getRootException(e), request, "NotFoundException");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> validationError(HttpMessageNotReadableException e, HttpServletRequest request) {
        return getException(HttpStatus.BAD_REQUEST, Constants.CONSTRAINT_VALIDATION_FAILED, e.getMostSpecificCause().getMessage(), request, "HttpMessageNotReadableException");
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<Object> handleException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        return getException(HttpStatus.METHOD_NOT_ALLOWED, ofNullable(e.getMessage()).orElse(e.toString()), ExceptionResolver.getRootException(e), request, "HttpRequestMethodNotSupportedException");
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<Object> handleException(IOException e, HttpServletRequest request) {
        return getException(HttpStatus.SERVICE_UNAVAILABLE, ofNullable(e.getMessage()).orElse(e.toString()), null, request, "IOException");
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<Object> handleException(MissingServletRequestParameterException e, HttpServletRequest request) {
        return getException(HttpStatus.BAD_REQUEST, Optional.of(e.getMessage()).orElse(e.toString()), ExceptionResolver.getRootException(e), request, "MissingServletRequestParameterException");
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exMethod, HttpServletRequest request) {
        String error = exMethod.getName() + " should be " + Objects.requireNonNull(exMethod.getRequiredType()).getName();
        return getException(HttpStatus.BAD_REQUEST, Constants.CONSTRAINT_VALIDATION_FAILED, error, request, "MethodArgumentTypeMismatchException");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validationError(MethodArgumentNotValidException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> fieldErrorDtos = fieldErrors.stream()
                .map(f -> f.getField().concat(":").concat(Objects.requireNonNull(f.getDefaultMessage()))).map(String::new).toList();
        return getException(HttpStatus.BAD_REQUEST, Constants.CONSTRAINT_VALIDATION_FAILED, fieldErrorDtos.toString(), request, "MethodArgumentNotValidException");
    }

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<Object> handleException(Throwable e, HttpServletRequest request) {
        if (e.getMessage().contains(Constants.DUPLICATION_CODE)) {
                return getException(HttpStatus.CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(), Constants.DUPLICATION_DESCRIPTION, request, "Throwable");
        }
        return getException(HttpStatus.INTERNAL_SERVER_ERROR, ofNullable(e.getMessage()).orElse(e.toString()), ExceptionResolver.getRootException(e), request, "Throwable");
    }

    @ExceptionHandler({NumberFormatException.class})
    public ResponseEntity<Object> handleException(NumberFormatException e, HttpServletRequest request) {
        return getException(HttpStatus.BAD_REQUEST, ofNullable(e.getMessage()).orElse(e.toString()), ExceptionResolver.getRootException(e), request, "NumberFormatException");
    }

    private String getTraceID() {
        return ofNullable(MDC.get(Constants.TRACE_ID_KEY)).orElse("not available");
    }

    private String getCurrentTimestamp() {
        Instant timestamp = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return timestamp.atZone(ZoneId.systemDefault()).format(formatter);
    }

    private ResponseEntity<Object> getException(HttpStatus httpStatus, String message, String description, HttpServletRequest path, String method) {
        BusinessException exception = BusinessException.builder()
                .httpStatusCode(httpStatus)
                .timestamp(getCurrentTimestamp())
                .status(httpStatus.value())
                .message(message)
                .description(description)
                .path(path.getRequestURI())
                .build();

        var responseHeaders = new HttpHeaders();
        responseHeaders.set(Constants.X_RD_TRACEID, getTraceID());

        if (HttpStatus.INTERNAL_SERVER_ERROR.value() == exception.getHttpStatusCode().value()) {
            log.error(Constants.LOG_KEY_METHOD + Constants.LOG_KEY_EVENT + Constants.LOG_KEY_HTTP_CODE + Constants.LOG_KEY_MESSAGE +
                    Constants.LOG_KEY_DESCRIPTION, method, Constants.LOG_EXCEPTION, exception.getHttpStatusCode().value(), exception.getMessage(), exception.getDescription(), exception);
        } else {
            log.error(Constants.LOG_KEY_METHOD + Constants.LOG_KEY_EVENT + Constants.LOG_KEY_HTTP_CODE + Constants.LOG_KEY_MESSAGE +
                    Constants.LOG_KEY_DESCRIPTION, method, Constants.LOG_EXCEPTION, exception.getHttpStatusCode().value(), exception.getMessage(), exception.getDescription());
        }

        return ResponseEntity.status(exception.getHttpStatusCode()).headers(responseHeaders).body(exception.getOnlyBody());
    }
}