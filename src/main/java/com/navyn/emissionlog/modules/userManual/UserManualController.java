package com.navyn.emissionlog.modules.userManual;

import com.navyn.emissionlog.Enums.Roles;
import com.navyn.emissionlog.modules.userManual.exceptions.UserManualNotFoundException;
import com.navyn.emissionlog.modules.users.services.JwtService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/user-manual")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class UserManualController {

    private final UserManualService userManualService;
    private final JwtService jwtService;

    @GetMapping("/download")
    @Operation(summary = "Download user manual", description = "Downloads the user manual PDF file. Available to all authenticated users.")
    public ResponseEntity<byte[]> downloadUserManual() {
        try {
            byte[] fileContent = userManualService.downloadUserManual();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "user-manual.pdf");
            headers.setContentLength(fileContent.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } catch (UserManualNotFoundException e) {
            log.warn("User manual not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            log.error("Error reading user manual file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload user manual", description = "Uploads or updates the user manual PDF file. Only ADMIN users can upload.")
    public ResponseEntity<ApiResponse> uploadUserManual(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            // Extract role from JWT token
            String role = extractRoleFromRequest(request);
            
            // Check if user is ADMIN
            if (role == null || !Roles.ADMIN.name().equals(role)) {
                throw new AccessDeniedException("Only ADMIN users can upload user manual");
            }

            userManualService.uploadUserManual(file);
            return ResponseEntity.ok(
                    new ApiResponse(true, "User manual uploaded successfully", null));
        } catch (AccessDeniedException e) {
            log.warn("Access denied for user manual upload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (IOException e) {
            log.error("Error uploading user manual", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to upload user manual", null));
        }
    }

    @GetMapping("/exists")
    @Operation(summary = "Check if user manual exists", description = "Returns whether a user manual file exists on the server.")
    public ResponseEntity<ApiResponse> checkUserManualExists() {
        boolean exists = userManualService.userManualExists();
        return ResponseEntity.ok(
                new ApiResponse(true, "User manual existence checked", exists));
    }

    private String extractRoleFromRequest(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Claims claims = jwtService.extractAllClaims(token);
                Object roleObj = claims.get("role");
                if (roleObj != null) {
                    return roleObj.toString();
                }
            }
        } catch (Exception e) {
            log.error("Error extracting role from token", e);
        }
        return null;
    }
}

