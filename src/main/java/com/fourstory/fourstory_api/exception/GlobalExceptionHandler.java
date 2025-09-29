package com.fourstory.fourstory_api.exception;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;

        List<Map<String, Object>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", fieldError.getDefaultMessage(),
                        "rejectedValue", maskPassword(fieldError.getField(), fieldError.getRejectedValue())
                ))
                .toList();

        ProblemDetail problemDetail = buildProblem(errorCode, "Validation failed (%d errors)".formatted(fieldErrors.size()), req);
        problemDetail.setProperty("errors", fieldErrors);


        logAt(errorCode.getHttpStatus(), "Validation errors: {}", fieldErrors);

        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMissingBody(HttpMessageNotReadableException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        String detail = "Request body is missing or malformed";
        ProblemDetail problemDetail = buildProblem(errorCode, detail, req);

        logAt(errorCode.getHttpStatus(), detail);

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        String detail = "Parameter '%s' has invalid value '%s'".formatted(ex.getName(), ex.getValue());
        ProblemDetail problemDetail = buildProblem(errorCode, detail, req);

        logAt(errorCode.getHttpStatus(), detail);

        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAILED;
        ProblemDetail problemDetail = buildProblem(errorCode, errorCode.getMessage(), req);

        logAt(errorCode.getHttpStatus(), "Authentication failed [{}]: {}", errorCode, errorCode.getMessage());

        return problemDetail;
    }

    @ExceptionHandler({ AuthorizationDeniedException.class, AccessDeniedException.class })
    public ProblemDetail handleAuthorizationAndAccessDeniedException(RuntimeException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        ProblemDetail problemDetail = buildProblem(errorCode, errorCode.getMessage(), req);

        logAt(errorCode.getHttpStatus(), "Access denied: {}", ex.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex, HttpServletRequest req) {
        ErrorCode errorCode = ex.getErrorCode();
        ProblemDetail problemDetail = buildProblem(errorCode, ex.getFormattedMessage(), req);

        logAt(errorCode.getHttpStatus(), "Business error [{}]: {}", ex.getErrorCode(), ex.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception", ex);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return buildProblem(errorCode, errorCode.getMessage(), req);
    }

    private ProblemDetail buildProblem(ErrorCode errorCode, String detail, HttpServletRequest req) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), detail);
        problemDetail.setTitle(errorCode.name());
        problemDetail.setProperty("code", errorCode.name());
        fillInstanceTimeTraceId(problemDetail, req);

        return problemDetail;
    }

    private void fillInstanceTimeTraceId(ProblemDetail problemDetail, HttpServletRequest req) {
        problemDetail.setInstance(URI.create(req.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());

        String traceId = Optional.ofNullable(req.getHeader("X-Request-ID")).orElse(MDC.get("traceId"));

        if (traceId != null) {
            problemDetail.setProperty("traceId", traceId);
        }
    }

    private Object maskPassword(String field, Object value) {
        if (value == null) return null;
        if (field == null) return value;

        if (field.toLowerCase().contains("password")) return "*****";

        return value;
    }

    private void logAt(HttpStatus httpStatus, String fmt, Object... args) {
        if (httpStatus.is5xxServerError()) {
            log.error(fmt, args);

            return;
        }
        if (httpStatus == HttpStatus.UNAUTHORIZED || httpStatus == HttpStatus.FORBIDDEN) {
            log.warn(fmt, args);

            return;
        }

        log.info(fmt, args);
    }
}
