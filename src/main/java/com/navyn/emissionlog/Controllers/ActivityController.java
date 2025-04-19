package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Enums.Sectors;
import com.navyn.emissionlog.Enums.TransportModes;
import com.navyn.emissionlog.Payload.Requests.CreateTransportActivityByFuelDto;
import com.navyn.emissionlog.Payload.Requests.CreateTransportActivityByVehicleDataDto;
import com.navyn.emissionlog.Payload.Requests.CreateStationaryActivityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.cglib.core.Local;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController("ActivityController")
@RequestMapping("/activities")
@SecurityRequirement(name = "BearerAuth")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Operation(summary = "Create a stationary activity", description = "Creates a stationary activity with the provided details.")
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

    @Operation(summary = "Create a transport activity by fuel", description = "Creates a transport activity using fuel data.")
    @PostMapping("/transport/fuel/create")
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

    @Operation(summary = "Create a transport activity by vehicle data", description = "Creates a transport activity using vehicle data.")
    @PostMapping("/transport/vehicleData/create")
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

    @Operation(summary = "Get an activity by ID", description = "Retrieves an activity using its unique identifier.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getActivity(@PathVariable("id") UUID id) {
        try {
            Activity activity = activityService.getActivityById(id);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Activity retrieved successfully", activity)
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Get all activities", description = "Retrieves a list of all activities and their emissions.")
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

    @Operation(summary = "Delete an activity", description = "Deletes an activity using its unique identifier.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteActivity(@PathVariable("id") UUID id) {
        try {
            activityService.deleteActivity(id);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Activity deleted successfully", null)
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Get all sectors", description = "Retrieves a list of all sectors.")
    @GetMapping("/sectors")
    public ResponseEntity<ApiResponse> getSectors() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "Sectors fetched successfully", Sectors.values()));
    }

    @Operation(summary = "Get all transport modes", description = "Retrieves a list of all transport modes.")
    @GetMapping("/transportModes")
    public ResponseEntity<ApiResponse> getTransportModes(){
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Transport Modes fetched successfully", TransportModes.values()));
    }

    @Operation(summary = "Get all stationary activities", description = "Retrieves a list of all stationary activities and their emissions.")
    @GetMapping("/stationary")
    public ResponseEntity<ApiResponse> getStationaryActivities(){
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Stationary activities fetched successfully", activityService.getStationaryActivities()));
    }

    @Operation(summary = "Get all transport activities", description = "Retrieves a list of all transport activities and their emissions.")
    @GetMapping("/transport")
    public ResponseEntity<ApiResponse> getTransportActivities(){
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Transport activities fetched succesfully", activityService.getTransportActivities()));
    }

    @Operation(summary = "Get dashboard summary", description = "Retrieves a summary of the dashboard.")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboardData(@RequestParam ("year") Integer year){
        if(year != null){
            LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(year, 12, 31, 23, 59);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Dashboard data fetched successfully", activityService.getDashboardData(startDate, endDate)));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Dashboard data fetched successfully", activityService.getDashboardData()));
    }

}