package com.navyn.emissionlog.modules.users;

import com.navyn.emissionlog.Enums.Roles;
import com.navyn.emissionlog.modules.auth.dtos.UpdateUserDTO;
import com.navyn.emissionlog.utils.ApiResponse;
import com.navyn.emissionlog.modules.users.services.JwtService;
import com.navyn.emissionlog.modules.users.services.UserServiceImpl;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController("UserController")
@RequestMapping(path ="/users")
@SecurityRequirement(name = "BearerAuth")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    JwtService jwtService;

    @Operation(summary = "Get all users", description = "Fetches all users available in the system. ADMIN only.")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers(HttpServletRequest request){
        try {
            String role = extractRoleFromRequest(request);
            if (role == null || !Roles.ADMIN.name().equals(role)) {
                throw new AccessDeniedException("Only ADMIN users can list all users");
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Users fetched Successfully", userService.getUsers()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Get user by email", description = "Fetches a user identified by the provided email.")
    @GetMapping(path="/{email}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable("email") String email){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "User Data Queried Successfully", userService.getUserByEmail(email)));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Update user by email", description = "Updates the user identified by the provided email. ADMIN only.")
    @PutMapping(path="/{email}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable("email") String email, @Valid @RequestBody UpdateUserDTO payload, HttpServletRequest request){
        try {
            String role = extractRoleFromRequest(request);
            if (role == null || !Roles.ADMIN.name().equals(role)) {
                throw new AccessDeniedException("Only ADMIN users can update users");
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "User Updated Successfully", userService.updateUser(email, payload)));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage(), null));
        }
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
        } catch (Exception ignored) {
        }
        return null;
    }
}