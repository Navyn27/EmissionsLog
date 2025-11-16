package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.service.SettlementTreesMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mitigation/settlementTrees")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class SettlementTreesMitigationController {
    
    private final SettlementTreesMitigationService service;
    
    @PostMapping
    @Operation(summary = "Create new settlement trees mitigation record")
    public ResponseEntity<ApiResponse> createSettlementTreesMitigation(
            @Valid @RequestBody SettlementTreesMitigationDto dto) {
        SettlementTreesMitigation mitigation = service.createSettlementTreesMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Settlement trees mitigation created successfully", 
            mitigation
        ));
    }
    
    @GetMapping
    @Operation(summary = "Get all settlement trees mitigation records")
    public ResponseEntity<ApiResponse> getAllSettlementTreesMitigation(
            @RequestParam(required = false, value = "year") Integer year) {
        List<SettlementTreesMitigation> mitigations = service.getAllSettlementTreesMitigation(year);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Settlement trees mitigation records fetched successfully", 
            mitigations
        ));
    }
}
