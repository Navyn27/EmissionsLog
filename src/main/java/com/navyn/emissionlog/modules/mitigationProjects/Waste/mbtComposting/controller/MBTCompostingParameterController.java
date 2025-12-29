package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.service.MBTCompostingParameterService;
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
@RequestMapping("/mitigation/mbtComposting/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class MBTCompostingParameterController {

    private final MBTCompostingParameterService service;

    @Operation(summary = "Create MBT Composting Parameter",
            description = "Creates a new MBT Composting Parameter with emission factor")
    @PostMapping
    public ResponseEntity<ApiResponse> createMBTCompostingParameter(
            @Valid @RequestBody MBTCompostingParameterDto dto) {
        MBTCompostingParameterResponseDto parameter = service.createMBTCompostingParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "MBT Composting Parameter created successfully", parameter));
    }

    @Operation(summary = "Update MBT Composting Parameter",
            description = "Updates an existing MBT Composting Parameter")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateMBTCompostingParameter(
            @PathVariable UUID id,
            @Valid @RequestBody MBTCompostingParameterDto dto) {
        MBTCompostingParameterResponseDto parameter = service.updateMBTCompostingParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "MBT Composting Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get MBT Composting Parameter by ID",
            description = "Retrieves a specific MBT Composting Parameter by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMBTCompostingParameterById(@PathVariable UUID id) {
        MBTCompostingParameterResponseDto parameter = service.getMBTCompostingParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "MBT Composting Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all MBT Composting Parameters",
            description = "Retrieves all MBT Composting Parameters ordered by creation date (newest first)")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllMBTCompostingParameters() {
        List<MBTCompostingParameterResponseDto> parameters = service.getAllMBTCompostingParameters();
        return ResponseEntity.ok(new ApiResponse(true, "MBT Composting Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active MBT Composting Parameter",
            description = "Retrieves the most recently created active MBT Composting Parameter")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestActive() {
        MBTCompostingParameterResponseDto parameter = service.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active MBT Composting Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable MBT Composting Parameter",
            description = "Disables an existing MBT Composting Parameter by setting isActive to false")
    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse> disableMBTCompostingParameter(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.ok(new ApiResponse(true, "MBT Composting Parameter disabled successfully", null));
    }

    @Operation(summary = "Delete MBT Composting Parameter",
            description = "Deletes an existing MBT Composting Parameter by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMBTCompostingParameter(@PathVariable UUID id) {
        service.deleteMBTCompostingParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "MBT Composting Parameter deleted successfully", null));
    }
}

