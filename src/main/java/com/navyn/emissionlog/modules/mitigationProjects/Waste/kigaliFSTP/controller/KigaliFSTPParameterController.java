package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.service.KigaliFSTPParameterService;
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
@RequestMapping("/mitigation/kigaliFSTP/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class KigaliFSTPParameterController {

    private final KigaliFSTPParameterService service;

    @Operation(summary = "Create Kigali FSTP Parameter", 
               description = "Creates a new Kigali FSTP Parameter for calculations")
    @PostMapping
    public ResponseEntity<ApiResponse> createKigaliFSTPParameter(@Valid @RequestBody KigaliFSTPParameterDto dto) {
        KigaliFSTPParameterResponseDto parameter = service.createKigaliFSTPParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Kigali FSTP Parameter created successfully", parameter));
    }

    @Operation(summary = "Update Kigali FSTP Parameter",
               description = "Updates an existing Kigali FSTP Parameter")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateKigaliFSTPParameter(
            @PathVariable UUID id,
            @Valid @RequestBody KigaliFSTPParameterDto dto) {
        KigaliFSTPParameterResponseDto parameter = service.updateKigaliFSTPParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali FSTP Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get Kigali FSTP Parameter by ID", 
               description = "Retrieves a specific Kigali FSTP Parameter by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getKigaliFSTPParameterById(@PathVariable UUID id) {
        KigaliFSTPParameterResponseDto parameter = service.getKigaliFSTPParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali FSTP Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all Kigali FSTP Parameters", 
               description = "Retrieves all Kigali FSTP Parameters, sorted with active ones first")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllKigaliFSTPParameters() {
        List<KigaliFSTPParameterResponseDto> parameters = service.getAllKigaliFSTPParameters();
        return ResponseEntity.ok(new ApiResponse(true, "Kigali FSTP Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Delete Kigali FSTP Parameter",
               description = "Deletes a Kigali FSTP Parameter by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteKigaliFSTPParameter(@PathVariable UUID id) {
        service.deleteKigaliFSTPParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali FSTP Parameter deleted successfully", null));
    }

    @Operation(summary = "Disable Kigali FSTP Parameter",
               description = "Disables a Kigali FSTP Parameter (sets isActive to false) without deleting it")
    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse> disableKigaliFSTPParameter(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali FSTP Parameter disabled successfully", null));
    }

    @Operation(summary = "Get latest active Kigali FSTP Parameter", 
               description = "Retrieves the latest active Kigali FSTP Parameter for calculations")
    @GetMapping("/latest-active")
    public ResponseEntity<ApiResponse> getLatestActiveKigaliFSTPParameter() {
        KigaliFSTPParameterResponseDto parameter = service.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active Kigali FSTP Parameter fetched successfully", parameter));
    }
}

