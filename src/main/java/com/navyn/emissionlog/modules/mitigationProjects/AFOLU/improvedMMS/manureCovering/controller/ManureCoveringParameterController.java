package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service.ManureCoveringParameterService;
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
@RequestMapping("/mitigation/manureCovering/parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Manure Covering Parameters", description = "API endpoints for managing Manure Covering Parameters")
public class ManureCoveringParameterController {

    private final ManureCoveringParameterService service;

    @Operation(summary = "Create a new Manure Covering Parameter", description = "Creates a new Manure Covering Parameter with the provided data")
    @PostMapping
    public ResponseEntity<ManureCoveringParameterResponseDto> create(
            @Valid @RequestBody ManureCoveringParameterDto dto) {
        ManureCoveringParameterResponseDto response = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Manure Covering Parameter by ID", description = "Retrieves a Manure Covering Parameter by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<ManureCoveringParameterResponseDto> getById(@PathVariable UUID id) {
        ManureCoveringParameterResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Manure Covering Parameters", description = "Retrieves all Manure Covering Parameters")
    @GetMapping
    public ResponseEntity<List<ManureCoveringParameterResponseDto>> getAll() {
        List<ManureCoveringParameterResponseDto> responses = service.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get latest active Manure Covering Parameter", description = "Retrieves the most recently created active Manure Covering Parameter")
    @GetMapping("/latest-active")
    public ResponseEntity<ManureCoveringParameterResponseDto> getLatestActive() {
        ManureCoveringParameterResponseDto response = service.getLatestActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Manure Covering Parameter", description = "Updates an existing Manure Covering Parameter with the provided data")
    @PutMapping("/{id}")
    public ResponseEntity<ManureCoveringParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody ManureCoveringParameterDto dto) {
        ManureCoveringParameterResponseDto response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Disable Manure Covering Parameter", description = "Disables a Manure Covering Parameter by setting isActive to false (soft delete)")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.noContent().build();
    }
}
