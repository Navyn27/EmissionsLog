package com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.controller;

import com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.dtos.ZeroTillageMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.models.ZeroTillageMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.service.ZeroTillageMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mitigation/zeroTillage")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ZeroTillageMitigationController {
    
    private final ZeroTillageMitigationService service;
    
    @PostMapping
    @Operation(summary = "Create new zero tillage mitigation record")
    public ResponseEntity<ApiResponse> createZeroTillageMitigation(
            @Valid @RequestBody ZeroTillageMitigationDto dto) {
        ZeroTillageMitigation mitigation = service.createZeroTillageMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Zero tillage mitigation created successfully", 
            mitigation
        ));
    }
    
    @GetMapping
    @Operation(summary = "Get all zero tillage mitigation records")
    public ResponseEntity<ApiResponse> getAllZeroTillageMitigation(
            @RequestParam(required = false, value = "year") Integer year) {
        List<ZeroTillageMitigation> mitigations = service.getAllZeroTillageMitigation(year);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Zero tillage mitigation records fetched successfully", 
            mitigations
        ));
    }
}
