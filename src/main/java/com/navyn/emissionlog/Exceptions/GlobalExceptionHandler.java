package com.navyn.emissionlog.Exceptions;

import com.navyn.emissionlog.utils.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("Response status exception: {}", ex.getReason());
        return new ResponseEntity<>(ApiResponse.error(ex.getReason()), ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        // Collect all field errors
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError(errors.get(0), errors));
    }

    private String formatFieldError(FieldError fieldError) {
        return String.format("%s",
                // fieldError.getField(),
                fieldError.getDefaultMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation: {}", ex.getMessage());

        String message;
        if (ex.getMessage() != null && ex.getMessage().contains("unique")) {
            if (ex.getMessage().toLowerCase().contains("year")) {
                message = "A record for this year already exists. Please use a different year or update the existing record.";
            } else if (ex.getMessage().toLowerCase().contains("email")) {
                message = "This email address is already registered. Please use a different email.";
            } else if (ex.getMessage().toLowerCase().contains("name")) {
                message = "A record with this name already exists. Please use a different name.";
            } else {
                message = "This record already exists. Please check for duplicates.";
            }
        } else if (ex.getMessage() != null && ex.getMessage().contains("foreign key")) {
            message = "Cannot delete this record because it is being used by other records.";
        } else {
            message = "Unable to save the record. Please check your input and try again.";
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        logger.warn("File upload size exceeded");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("The file you're trying to upload is too large. Maximum file size is 10MB."));
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedException(HttpClientErrorException.Unauthorized e) {
        logger.warn("Unauthorized access attempt");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("You are not authorized to access this resource. Please log in."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());

        // Sanitize message to avoid exposing internal details
        String message = ex.getMessage();
        if (message == null || message.isEmpty()) {
            message = "The provided data is invalid. Please check your input.";
        } else if (message.contains("not found") || message.contains("does not exist")) {
            message = "The requested resource was not found.";
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        logger.warn("Email already exists: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(
                        "This email address is already registered. Please use a different email or try logging in."));
    }

    @ExceptionHandler(UnmatchingPasswordsException.class)
    public ResponseEntity<ApiResponse> handleUnmatchingPasswordsException(UnmatchingPasswordsException ex) {
        logger.warn("Password mismatch for user");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("The passwords you entered do not match. Please try again."));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse
                        .error("We couldn't find an account with those credentials. Please check and try again."));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentialsException(BadCredentialsException ex) {
        logger.warn("Failed login attempt");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Incorrect email or password. Please try again."));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Authentication failed. Please check your credentials and try again."));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidTokenException(InvalidTokenException ex) {
        logger.warn("Invalid token: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Your session has expired or is invalid. Please log in again."));
    }

    @ExceptionHandler(NoSuchFieldException.class)
    public ResponseEntity<ApiResponse> handleNoSuchFieldException(NoSuchFieldException ex) {
        logger.error("Field not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid request. Please check your input and try again."));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.warn("Constraint violation: {}", ex.getMessage());

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.toList());

        String message = errors.isEmpty()
                ? "Please check your input and try again."
                : errors.get(0);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError(message, errors));
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiResponse> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {
        logger.error("Invalid data access: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("The request could not be processed. Please check your input and try again."));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse> handleIOException(IOException ex) {
        logger.error("File processing error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An error occurred while processing the file. Please try again."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime exception: {}", ex.getMessage(), ex);

        // Check if it's actually a user error (400) vs server error (500)
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (message != null) {
            // Preserve helpful business logic error messages (BAU, Zero Tillage Parameter, etc.)
            if (message.contains("BAU") || message.contains("Zero Tillage Parameter") || 
                message.contains("Zero Tillage Mitigation") || message.contains("Please create")) {
                // These are user-friendly business logic errors - preserve the full message
                status = HttpStatus.BAD_REQUEST;
                // Keep the original message as it contains helpful details for the user
            } else if (message.contains("not found") || message.contains("does not exist")) {
                status = HttpStatus.NOT_FOUND;
                message = "The requested resource was not found.";
            } else if (message.contains("already exists") || message.contains("duplicate")) {
                status = HttpStatus.CONFLICT;
                message = "This record already exists.";
            } else if (message.contains("Template") || message.contains("template") || message.contains("validation")
                    || message.contains("format")) {
                // Template/validation errors should be shown to user with full message
                status = HttpStatus.BAD_REQUEST;
                // Keep the original message as it contains helpful details
            } else if (message.contains("invalid") || message.contains("required")) {
                status = HttpStatus.BAD_REQUEST;
            } else {
                // Don't expose internal error details for unknown errors
                message = "An error occurred while processing your request. Please try again.";
            }
        } else {
            message = "An error occurred while processing your request. Please try again.";
        }

        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse> handleNullPointerException(NullPointerException ex) {
        logger.error("Null pointer exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An error occurred while processing your request. Please try again."));
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse> handleNumberFormatException(NumberFormatException ex) {
        logger.warn("Invalid number format: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid number format. Please enter a valid number."));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("The requested resource was not found."));
    }

    @ExceptionHandler(com.navyn.emissionlog.modules.userManual.exceptions.UserManualNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserManualNotFoundException(com.navyn.emissionlog.modules.userManual.exceptions.UserManualNotFoundException ex) {
        logger.warn("User manual not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User manual not found."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("You don't have permission to access this resource."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("Malformed JSON request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid request format. Please check your input."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
    }
}
