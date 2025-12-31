package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.controller;

import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleMitigationDto;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleMitigationResponseDto;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.service.ElectricVehicleMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mitigation/transport-scenarios/electric-vehicle")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ElectricVehicleMitigationController {
    
    private final ElectricVehicleMitigationService service;
    
    @Operation(summary = "Create Electric Vehicle mitigation record", 
               description = "Creates a new Electric Vehicle mitigation project record. Requires an active Electric Vehicle Parameter.")
    @PostMapping
    public ResponseEntity<ApiResponse> createElectricVehicleMitigation(
            @Valid @RequestBody ElectricVehicleMitigationDto dto) {
        ElectricVehicleMitigationResponseDto mitigation = service.createElectricVehicleMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Electric Vehicle mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Update Electric Vehicle mitigation record",
               description = "Updates an existing Electric Vehicle mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateElectricVehicleMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody ElectricVehicleMitigationDto dto) {
        ElectricVehicleMitigationResponseDto mitigation = service.updateElectricVehicleMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Electric Vehicle mitigation record updated successfully", mitigation));
    }
    
    @Operation(summary = "Get Electric Vehicle mitigation records", 
               description = "Retrieves all Electric Vehicle mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllElectricVehicleMitigation(
            @RequestParam(required = false) Integer year) {
        List<ElectricVehicleMitigationResponseDto> mitigations = service.getAllElectricVehicleMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Electric Vehicle mitigation records fetched successfully", mitigations));
    }
    
    @Operation(summary = "Delete Electric Vehicle mitigation record",
               description = "Deletes an existing Electric Vehicle mitigation record by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteElectricVehicleMitigation(@PathVariable UUID id) {
        service.deleteElectricVehicleMitigation(id);
        return ResponseEntity.ok(new ApiResponse(true, "Electric Vehicle mitigation record deleted successfully", null));
    }
}

