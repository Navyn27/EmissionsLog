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

    @Operation(summary = "Create Kigali WWTP Parameter",
            description = "Creates a new Kigali WWTP Parameter with methane emission factor, COD concentration, and CH4 GWP (100-year)")
    @PostMapping
    public ResponseEntity<ApiResponse> createKigaliWWTPParameter(
            @Valid @RequestBody KigaliWWTPParameterDto dto) {
        KigaliWWTPParameterResponseDto parameter = service.createKigaliWWTPParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Kigali WWTP Parameter created successfully", parameter));
    }

    @Operation(summary = "Update Kigali WWTP Parameter",
            description = "Updates an existing Kigali WWTP Parameter")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateKigaliWWTPParameter(
            @PathVariable UUID id,
            @Valid @RequestBody KigaliWWTPParameterDto dto) {
        KigaliWWTPParameterResponseDto parameter = service.updateKigaliWWTPParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get Kigali WWTP Parameter by ID",
            description = "Retrieves a specific Kigali WWTP Parameter by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getKigaliWWTPParameterById(@PathVariable UUID id) {
        KigaliWWTPParameterResponseDto parameter = service.getKigaliWWTPParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all Kigali WWTP Parameters",
            description = "Retrieves all Kigali WWTP Parameters ordered by creation date (newest first)")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllKigaliWWTPParameters() {
        List<KigaliWWTPParameterResponseDto> parameters = service.getAllKigaliWWTPParameters();
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active Kigali WWTP Parameter",
            description = "Retrieves the most recently created active Kigali WWTP Parameter")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestActive() {
        KigaliWWTPParameterResponseDto parameter = service.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active Kigali WWTP Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable Kigali WWTP Parameter",
            description = "Disables an existing Kigali WWTP Parameter by setting isActive to false")
    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse> disableKigaliWWTPParameter(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP Parameter disabled successfully", null));
    }

    @Operation(summary = "Delete Kigali WWTP Parameter",
            description = "Deletes an existing Kigali WWTP Parameter by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteKigaliWWTPParameter(@PathVariable UUID id) {
        service.deleteKigaliWWTPParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP Parameter deleted successfully", null));
    }
}

