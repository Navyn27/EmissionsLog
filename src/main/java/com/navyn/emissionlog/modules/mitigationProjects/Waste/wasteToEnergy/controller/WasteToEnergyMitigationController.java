package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToEnergyMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToEnergyMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service.WasteToEnergyMitigationService;
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
@RequestMapping("/mitigation/wasteToEnergy")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class WasteToEnergyMitigationController {
    
    private final WasteToEnergyMitigationService service;
    
    @Operation(summary = "Create Waste-to-Energy mitigation record", 
               description = "Creates a new Waste-to-Energy mitigation project record with automatic GHG reduction calculations")
    @PostMapping
    public ResponseEntity<ApiResponse> createWasteToEnergyMitigation(
            @Valid @RequestBody WasteToEnergyMitigationDto dto) {
        WasteToEnergyMitigation mitigation = service.createWasteToEnergyMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Waste-to-Energy mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Update Waste-to-Energy mitigation record",
               description = "Updates an existing Waste-to-Energy mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateWasteToEnergyMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody WasteToEnergyMitigationDto dto) {
        WasteToEnergyMitigation mitigation = service.updateWasteToEnergyMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Waste-to-Energy mitigation record updated successfully", mitigation));
    }
    
    @Operation(summary = "Get Waste-to-Energy mitigation records", 
               description = "Retrieves all Waste-to-Energy mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllWasteToEnergyMitigation(
            @RequestParam(required = false) Integer year) {
        List<WasteToEnergyMitigation> mitigations = service.getAllWasteToEnergyMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Waste-to-Energy mitigation records fetched successfully", mitigations));
    }
}
