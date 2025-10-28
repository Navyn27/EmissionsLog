package com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.controller;

import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.dtos.ImprovedMMSMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.models.ImprovedMMSMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.service.ImprovedMMSMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mitigation/improvedMMS")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ImprovedMMSMitigationController {
    
    private final ImprovedMMSMitigationService service;
    
    @PostMapping
    @Operation(summary = "Create new improved MMS mitigation record")
    public ResponseEntity<ApiResponse> createImprovedMMSMitigation(
            @Valid @RequestBody ImprovedMMSMitigationDto dto) {
        ImprovedMMSMitigation mitigation = service.createImprovedMMSMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Improved MMS mitigation created successfully", 
            mitigation
        ));
    }
    
    @GetMapping
    @Operation(summary = "Get all improved MMS mitigation records")
    public ResponseEntity<ApiResponse> getAllImprovedMMSMitigation(
            @RequestParam(required = false, value = "year") Integer year) {
        List<ImprovedMMSMitigation> mitigations = service.getAllImprovedMMSMitigation(year);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Improved MMS mitigation records fetched successfully", 
            mitigations
        ));
    }
}
