package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.service.ProtectiveForestParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/mitigation/protectiveForest/parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Protective Forest Parameters", description = "API endpoints for managing Protective Forest Parameters")
public class ProtectiveForestParameterController {

    private final ProtectiveForestParameterService protectiveForestParameterService;

    @Operation(summary = "Create a new Protective Forest Parameter", description = "Creates a new Protective Forest Parameter with the provided data")
    @PostMapping
    public ResponseEntity<ProtectiveForestParameterResponseDto> create(
            @Valid @RequestBody ProtectiveForestParameterDto dto) {
        ProtectiveForestParameterResponseDto response = protectiveForestParameterService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Protective Forest Parameter by ID", description = "Retrieves a Protective Forest Parameter by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<ProtectiveForestParameterResponseDto> getById(@PathVariable UUID id) {
        ProtectiveForestParameterResponseDto response = protectiveForestParameterService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Protective Forest Parameters", description = "Retrieves all Protective Forest Parameters")
    @GetMapping
    public ResponseEntity<List<ProtectiveForestParameterResponseDto>> getAll() {
        List<ProtectiveForestParameterResponseDto> responses = protectiveForestParameterService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get latest active Protective Forest Parameter", description = "Retrieves the most recently created active Protective Forest Parameter")
    @GetMapping("/latest-active")
    public ResponseEntity<ProtectiveForestParameterResponseDto> getLatestActive() {
        ProtectiveForestParameterResponseDto response = protectiveForestParameterService.getLatestActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Protective Forest Parameter", description = "Updates an existing Protective Forest Parameter with the provided data")
    @PutMapping("/{id}")
    public ResponseEntity<ProtectiveForestParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProtectiveForestParameterDto dto) {
        ProtectiveForestParameterResponseDto response = protectiveForestParameterService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Disable Protective Forest Parameter", description = "Disables a Protective Forest Parameter by setting isActive to false (soft delete)")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        protectiveForestParameterService.disable(id);
        return ResponseEntity.noContent().build();
    }
}
