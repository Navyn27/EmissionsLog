package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service.KigaliWWTPParameterService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mitigation/kigaliWWTP/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class KigaliWWTPParameterController {
    
    private final KigaliWWTPParameterService service;
    
    @Operation(summary = "Create Kigali WWTP parameter", 
               description = "Creates a new Kigali WWTP parameter with methane emission factor, COD concentration, and CH4 GWP values")
    @PostMapping
    public ResponseEntity<ApiResponse> createKigaliWWTPParameter(
            @Valid @RequestBody KigaliWWTPParameterDto dto) {
        KigaliWWTPParameterResponseDto parameter = service.createKigaliWWTPParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Kigali WWTP parameter created successfully", parameter));
    }
    
    @Operation(summary = "Update Kigali WWTP parameter",
               description = "Updates an existing Kigali WWTP parameter")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateKigaliWWTPParameter(
            @PathVariable UUID id,
            @Valid @RequestBody KigaliWWTPParameterDto dto) {
        KigaliWWTPParameterResponseDto parameter = service.updateKigaliWWTPParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP parameter updated successfully", parameter));
    }
    
    @Operation(summary = "Get Kigali WWTP parameter by ID", 
               description = "Retrieves a specific Kigali WWTP parameter by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getKigaliWWTPParameterById(@PathVariable UUID id) {
        KigaliWWTPParameterResponseDto parameter = service.getKigaliWWTPParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP parameter fetched successfully", parameter));
    }
    
    @Operation(summary = "Get all Kigali WWTP parameters", 
               description = "Retrieves all Kigali WWTP parameters, sorted by active status and creation date")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllKigaliWWTPParameters() {
        List<KigaliWWTPParameterResponseDto> parameters = service.getAllKigaliWWTPParameters();
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP parameters fetched successfully", parameters));
    }
    
    @Operation(summary = "Delete Kigali WWTP parameter",
               description = "Deletes an existing Kigali WWTP parameter by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteKigaliWWTPParameter(@PathVariable UUID id) {
        service.deleteKigaliWWTPParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP parameter deleted successfully", null));
    }
    
    @Operation(summary = "Disable Kigali WWTP parameter",
               description = "Disables an existing Kigali WWTP parameter (sets isActive to false)")
    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse> disableKigaliWWTPParameter(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP parameter disabled successfully", null));
    }
}

