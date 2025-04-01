package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Exceptions.EmailAlreadyExistsException;
import com.navyn.emissionlog.Exceptions.UnmatchingPasswordsException;
import com.navyn.emissionlog.Payload.Requests.LoginDTO;
import com.navyn.emissionlog.Payload.Requests.SignUpDTO;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @PostMapping(path="/signup")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpDTO user, BindingResult result) throws EmailAlreadyExistsException, UnmatchingPasswordsException {
        if(result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Invalid request data", null, result.getAllErrors()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true,"User created successfully",userService.registerUser(user)));
    }

    @PostMapping(path="/login")
    public ResponseEntity<ApiResponse> loginUser(@Valid @RequestBody LoginDTO user, BindingResult result){
        if(result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Invalid request data", null, result.getAllErrors()));
        }

        try{
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "User Logged In Successfully", userService.login(user)));
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage()));
        }
    }
}