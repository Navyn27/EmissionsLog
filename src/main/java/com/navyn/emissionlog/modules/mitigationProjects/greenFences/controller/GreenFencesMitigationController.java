package com.navyn.emissionlog.modules.mitigationProjects.greenFences.controller;

import com.navyn.emissionlog.modules.mitigationProjects.greenFences.dtos.GreenFencesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.greenFences.models.GreenFencesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.greenFences.service.GreenFencesMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mitigation/greenFences")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class GreenFencesMitigationController {
    
    private final GreenFencesMitigationService service;
    
    @PostMapping
    @Operation(summary = "Create new green fences mitigation record")
    public ResponseEntity<ApiResponse> createGreenFencesMitigation(
            @Valid @RequestBody GreenFencesMitigationDto dto) {
        GreenFencesMitigation mitigation = service.createGreenFencesMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Green fences mitigation created successfully", 
            mitigation
        ));
    }
    
    @GetMapping
    @Operation(summary = "Get all green fences mitigation records")
    public ResponseEntity<ApiResponse> getAllGreenFencesMitigation(
            @RequestParam(required = false, value = "year") Integer year) {
        List<GreenFencesMitigation> mitigations = service.getAllGreenFencesMitigation(year);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Green fences mitigation records fetched successfully", 
            mitigations
        ));
    }
}
