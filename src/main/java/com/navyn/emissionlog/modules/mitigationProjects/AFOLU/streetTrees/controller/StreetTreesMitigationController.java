package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.models.StreetTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.service.StreetTreesMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mitigation/streetTrees")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class StreetTreesMitigationController {
    
    private final StreetTreesMitigationService service;
    
    @PostMapping
    @Operation(summary = "Create new street trees mitigation record")
    public ResponseEntity<ApiResponse> createStreetTreesMitigation(
            @Valid @RequestBody StreetTreesMitigationDto dto) {
        StreetTreesMitigation mitigation = service.createStreetTreesMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Street trees mitigation created successfully", 
            mitigation
        ));
    }
    
    @GetMapping
    @Operation(summary = "Get all street trees mitigation records")
    public ResponseEntity<ApiResponse> getAllStreetTreesMitigation(
            @RequestParam(required = false, value = "year") Integer year) {
        List<StreetTreesMitigation> mitigations = service.getAllStreetTreesMitigation(year);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Street trees mitigation records fetched successfully", 
            mitigations
        ));
    }
}
