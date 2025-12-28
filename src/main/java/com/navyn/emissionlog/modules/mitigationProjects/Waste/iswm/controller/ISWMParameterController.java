package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service.ISWMParameterService;
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
@RequestMapping("/mitigation/iswm/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ISWMParameterController {

    private final ISWMParameterService service;

    @Operation(summary = "Create ISWM Parameter",
            description = "Creates a new ISWM Parameter with degradable organic fraction, landfill avoidance, and composting emission factor")
    @PostMapping
    public ResponseEntity<ApiResponse> createISWMParameter(
            @Valid @RequestBody ISWMParameterDto dto) {
        ISWMParameterResponseDto parameter = service.createISWMParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "ISWM Parameter created successfully", parameter));
    }

    @Operation(summary = "Update ISWM Parameter",
            description = "Updates an existing ISWM Parameter")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateISWMParameter(
            @PathVariable UUID id,
            @Valid @RequestBody ISWMParameterDto dto) {
        ISWMParameterResponseDto parameter = service.updateISWMParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "ISWM Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get ISWM Parameter by ID",
            description = "Retrieves a specific ISWM Parameter by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getISWMParameterById(@PathVariable UUID id) {
        ISWMParameterResponseDto parameter = service.getISWMParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "ISWM Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all ISWM Parameters",
            description = "Retrieves all ISWM Parameters ordered by creation date (newest first)")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllISWMParameters() {
        List<ISWMParameterResponseDto> parameters = service.getAllISWMParameters();
        return ResponseEntity.ok(new ApiResponse(true, "ISWM Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active ISWM Parameter",
            description = "Retrieves the most recently created active ISWM Parameter")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestActive() {
        ISWMParameterResponseDto parameter = service.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active ISWM Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable ISWM Parameter",
            description = "Disables an existing ISWM Parameter by setting isActive to false")
    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse> disableISWMParameter(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.ok(new ApiResponse(true, "ISWM Parameter disabled successfully", null));
    }

    @Operation(summary = "Delete ISWM Parameter",
            description = "Deletes an existing ISWM Parameter by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteISWMParameter(@PathVariable UUID id) {
        service.deleteISWMParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "ISWM Parameter deleted successfully", null));
    }
}

