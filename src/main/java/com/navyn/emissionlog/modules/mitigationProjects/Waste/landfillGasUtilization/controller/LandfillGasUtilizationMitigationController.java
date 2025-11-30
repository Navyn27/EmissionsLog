package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasUtilizationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasUtilizationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service.LandfillGasUtilizationMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mitigation/landfillGasUtilization")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class LandfillGasUtilizationMitigationController {
    
    private final LandfillGasUtilizationMitigationService service;
    
    @Operation(summary = "Create Landfill Gas Utilization mitigation record", 
               description = "Creates a new Landfill Gas Utilization mitigation project record for Kigali City with automatic calculations (project active after 2028)")
    @PostMapping
    public ResponseEntity<ApiResponse> createLandfillGasUtilizationMitigation(
            @Valid @RequestBody LandfillGasUtilizationMitigationDto dto) {
        LandfillGasUtilizationMitigation mitigation = service.createLandfillGasUtilizationMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Landfill Gas Utilization mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Update Landfill Gas Utilization mitigation record",
               description = "Updates an existing Landfill Gas Utilization mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateLandfillGasUtilizationMitigation(
            @PathVariable Long id,
            @Valid @RequestBody LandfillGasUtilizationMitigationDto dto) {
        LandfillGasUtilizationMitigation mitigation = service.updateLandfillGasUtilizationMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Landfill Gas Utilization mitigation record updated successfully", mitigation));
    }
    
    @Operation(summary = "Get Landfill Gas Utilization mitigation records", 
               description = "Retrieves all Landfill Gas Utilization mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllLandfillGasUtilizationMitigation(
            @RequestParam(required = false) Integer year) {
        List<LandfillGasUtilizationMitigation> mitigations = service.getAllLandfillGasUtilizationMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Landfill Gas Utilization mitigation records fetched successfully", mitigations));
    }
}
