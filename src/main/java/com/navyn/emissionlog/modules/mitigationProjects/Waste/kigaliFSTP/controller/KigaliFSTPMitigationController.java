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
    
    @Operation(summary = "Get Kigali FSTP mitigation records", 
               description = "Retrieves all Kigali FSTP mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllKigaliFSTPMitigation(
            @RequestParam(required = false) Integer year) {
        List<KigaliFSTPMitigation> mitigations = service.getAllKigaliFSTPMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali FSTP mitigation records fetched successfully", mitigations));
    }
}
