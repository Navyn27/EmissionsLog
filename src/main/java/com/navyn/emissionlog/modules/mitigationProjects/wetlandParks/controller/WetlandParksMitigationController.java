package com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.controller;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.dtos.WetlandParksMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.models.WetlandParksMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.service.WetlandParksMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mitigation/wetlandParks")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class WetlandParksMitigationController {
    
    private final WetlandParksMitigationService service;
    
    @PostMapping
    @Operation(summary = "Create new wetland parks mitigation record")
    public ResponseEntity<ApiResponse> createWetlandParksMitigation(
            @Valid @RequestBody WetlandParksMitigationDto dto) {
        WetlandParksMitigation mitigation = service.createWetlandParksMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Wetland parks mitigation created successfully", 
            mitigation
        ));
    }
    
    @GetMapping
    @Operation(summary = "Get all wetland parks mitigation records")
    public ResponseEntity<ApiResponse> getAllWetlandParksMitigation(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "category") WetlandTreeCategory category) {
        List<WetlandParksMitigation> mitigations = service.getAllWetlandParksMitigation(year, category);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Wetland parks mitigation records fetched successfully", 
            mitigations
        ));
    }
}
