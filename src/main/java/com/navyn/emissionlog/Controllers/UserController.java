package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Payload.Requests.SignUpDTO;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.ServiceImpls.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path ="")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @GetMapping(path="/users")
    public ResponseEntity<ApiResponse> getAllUsers(HttpServletRequest request){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Users fetched Successfully", userService.getUsers()));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping(path="/user/{email}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable String email){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "User Data Queried Successfully", userService.getUser(email)));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping(path="/user/{email}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable String email, @Valid @RequestBody SignUpDTO payload){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "User Updated Successfully", userService.updateUser(email, payload)));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}