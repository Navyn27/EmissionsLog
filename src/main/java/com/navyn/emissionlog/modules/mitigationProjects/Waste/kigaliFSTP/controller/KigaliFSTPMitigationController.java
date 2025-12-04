package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models.KigaliFSTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.service.KigaliFSTPMitigationService;
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
@RequestMapping("/mitigation/kigaliFSTP")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class KigaliFSTPMitigationController {
    
    private final KigaliFSTPMitigationService service;
    
    @Operation(summary = "Create Kigali FSTP mitigation record", 
               description = "Creates a new Kigali Fecal Sludge Treatment Plant (FSTP) mitigation project record at Masaka with phase-based capacity calculations")
    @PostMapping
    public ResponseEntity<ApiResponse> createKigaliFSTPMitigation(
            @Valid @RequestBody KigaliFSTPMitigationDto dto) {
        KigaliFSTPMitigation mitigation = service.createKigaliFSTPMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Kigali FSTP mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Update Kigali FSTP mitigation record",
               description = "Updates an existing Kigali FSTP mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateKigaliFSTPMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody KigaliFSTPMitigationDto dto) {
        KigaliFSTPMitigation mitigation = service.updateKigaliFSTPMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali FSTP mitigation record updated successfully", mitigation));
    }
    
    @Operation(summary = "Get Kigali FSTP mitigation records", 
               description = "Retrieves all Kigali FSTP mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllKigaliFSTPMitigation(
            @RequestParam(required = false) Integer year) {
        List<KigaliFSTPMitigation> mitigations = service.getAllKigaliFSTPMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali FSTP mitigation records fetched successfully", mitigations));
    }
    
    @Operation(summary = "Delete Kigali FSTP mitigation record",
               description = "Deletes an existing Kigali FSTP mitigation record by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteKigaliFSTPMitigation(@PathVariable UUID id) {
        service.deleteKigaliFSTPMitigation(id);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali FSTP mitigation record deleted successfully", null));
    }
}
