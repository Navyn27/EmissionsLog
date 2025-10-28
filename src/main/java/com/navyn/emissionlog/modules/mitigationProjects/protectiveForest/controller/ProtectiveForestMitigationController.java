package com.navyn.emissionlog.modules.mitigationProjects.protectiveForest.controller;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import com.navyn.emissionlog.modules.mitigationProjects.protectiveForest.dtos.ProtectiveForestMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.protectiveForest.models.ProtectiveForestMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.protectiveForest.service.ProtectiveForestMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mitigation/protectiveForest")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ProtectiveForestMitigationController {
    
    private final ProtectiveForestMitigationService service;
    
    @PostMapping
    @Operation(summary = "Create new protective forest mitigation record")
    public ResponseEntity<ApiResponse> createProtectiveForestMitigation(
            @Valid @RequestBody ProtectiveForestMitigationDto dto) {
        ProtectiveForestMitigation mitigation = service.createProtectiveForestMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Protective forest mitigation created successfully", 
            mitigation
        ));
    }
    
    @GetMapping
    @Operation(summary = "Get all protective forest mitigation records")
    public ResponseEntity<ApiResponse> getAllProtectiveForestMitigation(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "category") ProtectiveForestCategory category) {
        List<ProtectiveForestMitigation> mitigations = 
            service.getAllProtectiveForestMitigation(year, category);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Protective forest mitigation records fetched successfully", 
            mitigations
        ));
    }
}
