package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service.ISWMMitigationService;
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
@RequestMapping("/mitigation/iswm")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ISWMMitigationController {
    
    private final ISWMMitigationService service;
    
    @Operation(summary = "Create ISWM mitigation record", 
               description = "Creates a new Integrated Solid Waste Management (ISWM) mitigation project record")
    @PostMapping
    public ResponseEntity<ApiResponse> createISWMMitigation(
            @Valid @RequestBody ISWMMitigationDto dto) {
        ISWMMitigation mitigation = service.createISWMMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "ISWM mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Update ISWM mitigation record",
               description = "Updates an existing ISWM mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateISWMMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody ISWMMitigationDto dto) {
        ISWMMitigation mitigation = service.updateISWMMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "ISWM mitigation record updated successfully", mitigation));
    }
    
    @Operation(summary = "Get ISWM mitigation records", 
               description = "Retrieves all Integrated Solid Waste Management mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllISWMMitigation(
            @RequestParam(required = false) Integer year) {
        List<ISWMMitigation> mitigations = service.getAllISWMMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "ISWM mitigation records fetched successfully", mitigations));
    }
}
