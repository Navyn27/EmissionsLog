package com.navyn.emissionlog.Payload.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private Boolean success;
    private String message;
    private Object data;
    private List<String> errors;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(boolean success, String message, Object data, List<ObjectError> errors){
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors.stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

    }

    public static ApiResponse validationError(String message, List<String> errors) {
        return new ApiResponse(false, message, null, errors);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message);
    }
}
