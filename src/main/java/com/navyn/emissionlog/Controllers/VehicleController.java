package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vehicles")
@SecurityRequirement(name = "BearerAuth")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Operation(summary = "Get all vehicles")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllVehicles() {
        return ResponseEntity.ok(new ApiResponse(true, "Vehicles Fetched Successfully", vehicleService.getAllVehicles()));
    }
}

