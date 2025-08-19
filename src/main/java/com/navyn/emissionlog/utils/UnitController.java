package com.navyn.emissionlog.utils;

import com.navyn.emissionlog.Enums.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("UnitsController")
@SecurityRequirement(name = "BearerAuth")
@RequestMapping(value = "/units")
public class UnitController {

    @Operation(summary = "Retrieves units by Metric", description = "Fetches supported units associated with the specified metric.")
    @GetMapping("/{metric}")
    public ResponseEntity<ApiResponse> getFuelUnitsByMetric(@PathVariable("metric") Metrics metric){
        switch(metric){
            case MASS:
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Mass Units Fetched successfully", MassUnits.values()));
            case VOLUME:
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Volume Units Fetched successfully", VolumeUnits.values()));
            case ENERGY:
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Energy Units Fetched successfully", EnergyUnits.values()));
            case DISTANCE:
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Distance Units Fetched successfully", DistanceUnits.values()));
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Invalid Metric", null));
        }
    }
}
