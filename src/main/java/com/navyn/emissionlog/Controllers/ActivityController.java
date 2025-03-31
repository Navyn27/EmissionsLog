package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Payload.Requests.CreateTransportActivityByFuelDto;
import com.navyn.emissionlog.Payload.Requests.CreateTransportActivityByVehicleDataDto;
import com.navyn.emissionlog.Payload.Requests.CreateStationaryActivityDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activities")
@SecurityRequirement(name = "BearerAuth")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping("/stationary/create")
    public ResponseEntity<ApiResponse> createStationaryActivity(@Valid @RequestBody CreateStationaryActivityDto activityDto) {
        try {
            Activity createdActivity = activityService.createStationaryActivity(activityDto);
            return new ResponseEntity<>(
                    new ApiResponse(true, "Activity created successfully", createdActivity),
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/mobile/fuel/create")
    public ResponseEntity<ApiResponse> createMobileFuelActivity(@Valid @RequestBody CreateTransportActivityByFuelDto activityDto) {
        try {
            Activity createdActivity = activityService.createTransportActivityByFuel(activityDto);
            return new ResponseEntity<>(
                    new ApiResponse(true, "Activity created successfully", createdActivity),
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/mobile/vehicle-data/create")
    public ResponseEntity<ApiResponse> createMobileVehicleDataActivity(@Valid @RequestBody CreateTransportActivityByVehicleDataDto activityDto){
        try{
            Activity createdActivity = activityService.createTransportActivityByVehicleData(activityDto);
            return new ResponseEntity<>(
                    new ApiResponse(true, "Activity created successfully", createdActivity),
                    HttpStatus.CREATED
            );
        }
        catch(IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getActivity(@PathVariable UUID id) {
        try {
            Activity activity = activityService.getActivityById(id);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Activity retrieved successfully", activity)
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllActivities() {
        List<Activity> activities = activityService.getAllActivities();
        return ResponseEntity.ok(
                new ApiResponse(true, "Activities retrieved successfully", activities)
        );
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse> updateActivity(@PathVariable UUID id, @Valid @RequestBody Activity activity) {
//        try {
//            Activity updatedActivity = activityService.updateActivity(id, activity);
//            return ResponseEntity.ok(
//                    new ApiResponse(true, "Activity updated successfully", updatedActivity)
//            );
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        }
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteActivity(@PathVariable UUID id) {
        try {
            activityService.deleteActivity(id);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Activity deleted successfully", null)
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse> handleResponseStatusException(ResponseStatusException e) {
        return new ResponseEntity<>(
                new ApiResponse(false, e.getMessage(), null),
                e.getStatusCode()
        );
    }
}