package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRPlasticWasteMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models.EPRPlasticWasteMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.service.EPRPlasticWasteMitigationService;
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
@RequestMapping("/mitigation/eprPlasticWaste")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class EPRPlasticWasteMitigationController {
    
    private final EPRPlasticWasteMitigationService service;
    
    @Operation(summary = "Create EPR Circular Economy Plastic Waste mitigation record", 
               description = "Creates a new EPR Plastic Waste mitigation project record for Kigali with year-over-year growth tracking. For the first year, 'plasticWasteBaseTonnesPerYear' must be provided. For subsequent years, it's calculated from the previous year's data.")
    @PostMapping
    public ResponseEntity<ApiResponse> createEPRPlasticWasteMitigation(
            @Valid @RequestBody EPRPlasticWasteMitigationDto dto) {
        EPRPlasticWasteMitigation mitigation = service.createEPRPlasticWasteMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "EPR Plastic Waste mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Update EPR Plastic Waste mitigation record",
               description = "Updates an existing EPR Plastic Waste mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEPRPlasticWasteMitigation(
            @PathVariable Long id,
            @Valid @RequestBody EPRPlasticWasteMitigationDto dto) {
        EPRPlasticWasteMitigation mitigation = service.updateEPRPlasticWasteMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "EPR Plastic Waste mitigation record updated successfully", mitigation));
    }
    
    @Operation(summary = "Get EPR Plastic Waste mitigation records", 
               description = "Retrieves all EPR Plastic Waste mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllEPRPlasticWasteMitigation(
            @RequestParam(required = false) Integer year) {
        List<EPRPlasticWasteMitigation> mitigations = service.getAllEPRPlasticWasteMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "EPR Plastic Waste mitigation records fetched successfully", mitigations));
    }
}
