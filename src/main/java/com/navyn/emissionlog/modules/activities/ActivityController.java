package com.navyn.emissionlog.modules.activities;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Enums.Fuel.FuelTypes;
import com.navyn.emissionlog.Enums.Transport.TransportModes;
import com.navyn.emissionlog.Enums.Transport.TransportType;
import com.navyn.emissionlog.modules.activities.dtos.CreateTransportActivityByFuelDto;
import com.navyn.emissionlog.modules.activities.dtos.CreateTransportActivityByVehicleDataDto;
import com.navyn.emissionlog.modules.activities.dtos.CreateStationaryActivityDto;
import com.navyn.emissionlog.modules.activities.dtos.UpdateTransportActivityByFuelDto;
import com.navyn.emissionlog.modules.activities.dtos.UpdateTransportActivityByVehicleDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.navyn.emissionlog.modules.activities.models.Activity;
import com.navyn.emissionlog.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController("ActivityController")
@RequestMapping("/activities")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ActivityController {

    private final ActivityService activityService;

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

    @Operation(summary = "Update a transport activity by fuel", description = "Updates a transport activity that was created using fuel data.")
    @PutMapping("/transport/fuel/update/{id}")
    public ResponseEntity<ApiResponse> updateTransportActivityByFuel(@PathVariable("id") UUID id, @Valid @RequestBody UpdateTransportActivityByFuelDto activityDto) {
        try {
            Activity updatedActivity = activityService.updateTransportActivityByFuel(id, activityDto);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Transport activity updated successfully", updatedActivity)
            );
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Update a transport activity by vehicle data", description = "Updates a transport activity that was created using vehicle data.")
    @PutMapping("/transport/vehicleData/update/{id}")
    public ResponseEntity<ApiResponse> updateTransportActivityByVehicleData(@PathVariable("id") UUID id, @Valid @RequestBody UpdateTransportActivityByVehicleDataDto activityDto) {
        try {
            Activity updatedActivity = activityService.updateTransportActivityByVehicleData(id, activityDto);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Transport activity updated successfully", updatedActivity)
            );
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Delete a transport activity", description = "Deletes a transport activity using its unique identifier.")
    @DeleteMapping("/transport/delete/{id}")
    public ResponseEntity<ApiResponse> deleteTransportActivity(@PathVariable("id") UUID id) {
        try {
            activityService.deleteTransportActivity(id);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Transport activity deleted successfully", null)
            );
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
//    @PostMapping("/waste/solid/create")
//    public ResponseEntity<ApiResponse> createSolidWasteActivity(@Valid @RequestBody CreateSolidWasteActivityDto activityDto) {
//        try {
//            Activity createdActivity = activityService.createSolidWasteActivity(activityDto);
//            return new ResponseEntity<>(
//                    new ApiResponse(true, "Activity created successfully", createdActivity),
//                    HttpStatus.CREATED
//            );
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @PostMapping("/waste/biologicallyTreated/create")
//    public ResponseEntity<ApiResponse> createBiologicallTreatedWasteActivity(@Valid @RequestBody CreateBiologicallTreatedWasteDto activityDto){
//        try{
//            Activity createdActivity = activityService.createBiologicallyTreatedWasteActivity(activityDto);
//            return new ResponseEntity<>(
//                    new ApiResponse(true, "Activity created successfully", createdActivity),
//                    HttpStatus.CREATED
//            );
//        }
//        catch(IllegalArgumentException e){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @PostMapping("/waste/burning/create")
//
//    @PostMapping("/waste/incineration/create")

    @Operation(summary = "Get an activity by ID", description = "Retrieves an activity using its unique identifier.")
    @GetMapping("/id/{id}")
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
//
//    @PutMapping("/id/{id}")
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
    @DeleteMapping("/id/{id}")
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
    public ResponseEntity<ApiResponse> getStationaryActivities(@RequestParam(required = false, value = "sector") Sectors sector, @RequestParam(required = false, value = "region") UUID region, @RequestParam(required = false, value = "fuel") UUID fuel, @RequestParam(required = false, value = "fuelType") FuelTypes fuelType, @RequestParam(required = false, value = "year") Integer year) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Stationary activities fetched successfully", activityService.getStationaryActivities(region, sector, fuel, fuelType, year)));
    }

    @Operation(summary = "Get all transport activities", description = "Retrieves a list of all transport activities and their emissions.")
    @GetMapping("/transport")
    public ResponseEntity<ApiResponse> getTransportActivities(@RequestParam(required = false, value = "transportMode") TransportModes transportMode, @RequestParam(required = false, value = "region") UUID region, @RequestParam(required = false, value = "transportType") TransportType transportType, @RequestParam(required = false, value = "fuel") UUID fuel, @RequestParam(required = false, value = "fuelType") FuelTypes fuelType, @RequestParam(required = false, value = "vehicle") UUID vehicle, @RequestParam(required = false, value = "scope") Scopes scope, @RequestParam(required = false, value = "year") Integer year){
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Transport activities fetched succesfully", activityService.getTransportActivities(transportMode, region, transportType, fuel, fuelType, vehicle, scope, year)));
    }

    @Operation(summary = "Get dashboard summary", description = "Retrieves a summary of the dashboard.")
    @GetMapping("/stats/dashboard")
    public ResponseEntity<ApiResponse> getDashboardData(@RequestParam (required = false, value = "startingYear") Integer startingYear, @RequestParam (required = false, value = "endingYear") Integer endingYear){
        if(startingYear != null || endingYear != null) {
            LocalDateTime startDate = startingYear != null ? LocalDateTime.of(startingYear, 1, 1, 0, 0) : LocalDateTime.of(endingYear, 1, 1, 0, 0);
            LocalDateTime endDate = endingYear != null ? LocalDateTime.of(endingYear, 12, 31, 23, 59) : LocalDateTime.of(startingYear, 12, 31, 23, 59);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Dashboard data fetched successfully", activityService.getDashboardData(startDate, endDate)));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Dashboard data fetched successfully", activityService.getDashboardData()));
    }

    @Operation(summary = "Get dashboard graph data by Month", description = "Retrieves data for the dashboard graph by month.")
    @GetMapping("/stats/dashboard/graph/groupedByMonth")
    public ResponseEntity<ApiResponse> getDashboardGraphDataByMonth(@RequestParam (required = false, value = "year") Integer year){
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Dashboard graph data fetched successfully", activityService.getDashboardGraphDataByMonth(Objects.requireNonNullElseGet(year, () -> LocalDateTime.now().getYear()))));
    }

    @Operation(summary = "Get dashboard graph data by Year", description = "Retrieves data for the dashboard graph by year.")
    @GetMapping("stats/dashboard/graph/groupedByYear")
    public ResponseEntity<ApiResponse> getDashboardGraphDataByYear(@RequestParam ( value = "startingYear") Integer startingYear, @RequestParam ("endingYear") Integer endingYear){
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Dashboard graph data fetched successfully", activityService.getDashboardGraphDataByYear(startingYear, endingYear)));
    }

    @Operation(summary = "Get all countries", description = "Retrieves a list of all countries.")
    @GetMapping("/countries")
    public ResponseEntity<ApiResponse> getCountries() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "Countries fetched successfully", Countries.values()));
    }
    
    // ============= MINI DASHBOARDS =============
    
    @Operation(summary = "Get Transport dashboard summary", description = "Retrieves transport emissions summary.")
    @GetMapping("/transport/dashboard/summary")
    public ResponseEntity<ApiResponse> getTransportDashboardSummary(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Transport dashboard summary fetched successfully", 
                        activityService.getTransportDashboardSummary(startingYear, endingYear)));
    }
    
    @Operation(summary = "Get Transport dashboard graph", description = "Retrieves transport emissions graph data by year.")
    @GetMapping("/transport/dashboard/graph")
    public ResponseEntity<ApiResponse> getTransportDashboardGraph(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Transport dashboard graph fetched successfully", 
                        activityService.getTransportDashboardGraph(startingYear, endingYear)));
    }
    
    @Operation(summary = "Get Stationary dashboard summary", description = "Retrieves stationary emissions summary.")
    @GetMapping("/stationary/dashboard/summary")
    public ResponseEntity<ApiResponse> getStationaryDashboardSummary(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Stationary dashboard summary fetched successfully", 
                        activityService.getStationaryDashboardSummary(startingYear, endingYear)));
    }
    
    @Operation(summary = "Get Stationary dashboard graph", description = "Retrieves stationary emissions graph data by year.")
    @GetMapping("/stationary/dashboard/graph")
    public ResponseEntity<ApiResponse> getStationaryDashboardGraph(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Stationary dashboard graph fetched successfully", 
                        activityService.getStationaryDashboardGraph(startingYear, endingYear)));
    }
}