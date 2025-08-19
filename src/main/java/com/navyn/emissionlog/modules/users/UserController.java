package com.navyn.emissionlog.modules.users;

import com.navyn.emissionlog.modules.auth.dtos.SignUpDTO;
import com.navyn.emissionlog.utils.ApiResponse;
import com.navyn.emissionlog.modules.users.services.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("UserController")
@RequestMapping(path ="/users")
@SecurityRequirement(name = "BearerAuth")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @Operation(summary = "Get all users", description = "Fetches all users available in the system.")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers(HttpServletRequest request){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Users fetched Successfully", userService.getUsers()));
        }
        catch(Exception e){
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

    @Operation(summary = "Update user by email", description = "Updates the user identified by the provided email with the new details.")
    @PutMapping(path="/{email}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable("email") String email, @Valid @RequestBody SignUpDTO payload){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "User Updated Successfully", userService.updateUser(email, payload)));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}