package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.service.EPRParameterService;
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
@RequestMapping("/mitigation/eprPlasticWaste/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class EPRParameterController {

    private final EPRParameterService service;

    @Operation(summary = "Create EPR Parameter",
            description = "Creates a new EPR Parameter with recycling rates and emission factor")
    @PostMapping
    public ResponseEntity<ApiResponse> createEPRParameter(
            @Valid @RequestBody EPRParameterDto dto) {
        EPRParameterResponseDto parameter = service.createEPRParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "EPR Parameter created successfully", parameter));
    }

    @Operation(summary = "Update EPR Parameter",
            description = "Updates an existing EPR Parameter")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEPRParameter(
            @PathVariable UUID id,
            @Valid @RequestBody EPRParameterDto dto) {
        EPRParameterResponseDto parameter = service.updateEPRParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "EPR Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get EPR Parameter by ID",
            description = "Retrieves a specific EPR Parameter by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEPRParameterById(@PathVariable UUID id) {
        EPRParameterResponseDto parameter = service.getEPRParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "EPR Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all EPR Parameters",
            description = "Retrieves all EPR Parameters ordered by creation date (newest first)")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllEPRParameters() {
        List<EPRParameterResponseDto> parameters = service.getAllEPRParameters();
        return ResponseEntity.ok(new ApiResponse(true, "EPR Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active EPR Parameter",
            description = "Retrieves the most recently created active EPR Parameter")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestActive() {
        EPRParameterResponseDto parameter = service.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active EPR Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable EPR Parameter",
            description = "Disables an existing EPR Parameter by setting isActive to false")
    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse> disableEPRParameter(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.ok(new ApiResponse(true, "EPR Parameter disabled successfully", null));
    }

    @Operation(summary = "Delete EPR Parameter",
            description = "Deletes an existing EPR Parameter by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEPRParameter(@PathVariable UUID id) {
        service.deleteEPRParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "EPR Parameter deleted successfully", null));
    }
}

