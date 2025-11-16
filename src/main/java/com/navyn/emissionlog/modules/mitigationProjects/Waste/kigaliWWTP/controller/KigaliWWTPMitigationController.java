package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service.KigaliWWTPMitigationService;
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
@RequestMapping("/mitigation/kigaliWWTP")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class KigaliWWTPMitigationController {
    
    private final KigaliWWTPMitigationService service;
    
    @Operation(summary = "Create Kigali WWTP mitigation record", 
               description = "Creates a new Kigali Wastewater Treatment Plant (WWTP) mitigation project record at GITICYINYONI with year-based household connection rates")
    @PostMapping
    public ResponseEntity<ApiResponse> createKigaliWWTPMitigation(
            @Valid @RequestBody KigaliWWTPMitigationDto dto) {
        KigaliWWTPMitigation mitigation = service.createKigaliWWTPMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Kigali WWTP mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Get Kigali WWTP mitigation records", 
               description = "Retrieves all Kigali WWTP mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllKigaliWWTPMitigation(
            @RequestParam(required = false) Integer year) {
        List<KigaliWWTPMitigation> mitigations = service.getAllKigaliWWTPMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP mitigation records fetched successfully", mitigations));
    }
}
