package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service.LandfillGasParameterService;
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
@RequestMapping("/mitigation/landfillGasUtilization/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class LandfillGasParameterController {

    private final LandfillGasParameterService service;

    @Operation(summary = "Create Landfill Gas Parameter",
            description = "Creates a new Landfill Gas Parameter with destruction efficiency percentage and global warming potential for CH₄")
    @PostMapping
    public ResponseEntity<ApiResponse> createLandfillGasParameter(
            @Valid @RequestBody LandfillGasParameterDto dto) {
        LandfillGasParameterResponseDto parameter = service.createLandfillGasParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Landfill Gas Parameter created successfully", parameter));
    }

    @Operation(summary = "Update Landfill Gas Parameter",
            description = "Updates an existing Landfill Gas Parameter")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateLandfillGasParameter(
            @PathVariable UUID id,
            @Valid @RequestBody LandfillGasParameterDto dto) {
        LandfillGasParameterResponseDto parameter = service.updateLandfillGasParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Landfill Gas Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get Landfill Gas Parameter by ID",
            description = "Retrieves a specific Landfill Gas Parameter by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getLandfillGasParameterById(@PathVariable UUID id) {
        LandfillGasParameterResponseDto parameter = service.getLandfillGasParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Landfill Gas Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all Landfill Gas Parameters",
            description = "Retrieves all Landfill Gas Parameters ordered by creation date (newest first)")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllLandfillGasParameters() {
        List<LandfillGasParameterResponseDto> parameters = service.getAllLandfillGasParameters();
        return ResponseEntity.ok(new ApiResponse(true, "Landfill Gas Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active Landfill Gas Parameter",
            description = "Retrieves the most recently created active Landfill Gas Parameter")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestActive() {
        LandfillGasParameterResponseDto parameter = service.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active Landfill Gas Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable Landfill Gas Parameter",
            description = "Disables an existing Landfill Gas Parameter by setting isActive to false")
    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse> disableLandfillGasParameter(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.ok(new ApiResponse(true, "Landfill Gas Parameter disabled successfully", null));
    }

    @Operation(summary = "Delete Landfill Gas Parameter",
            description = "Deletes an existing Landfill Gas Parameter by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteLandfillGasParameter(@PathVariable UUID id) {
        service.deleteLandfillGasParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "Landfill Gas Parameter deleted successfully", null));
    }
}
