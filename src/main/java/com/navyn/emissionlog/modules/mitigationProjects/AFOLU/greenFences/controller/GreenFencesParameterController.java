package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.service.GreenFencesParameterService;
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
@RequestMapping("/mitigation/greenFences/parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Green Fences Parameters", description = "API endpoints for managing Green Fences Parameters")
public class GreenFencesParameterController {
    
    private final GreenFencesParameterService greenFencesParameterService;

    @Operation(summary = "Create a new Green Fences Parameter", description = "Creates a new Green Fences Parameter with the provided data")
    @PostMapping
    public ResponseEntity<GreenFencesParameterResponseDto> create(@Valid @RequestBody GreenFencesParameterDto dto) {
        GreenFencesParameterResponseDto response = greenFencesParameterService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Green Fences Parameter by ID", description = "Retrieves a Green Fences Parameter by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<GreenFencesParameterResponseDto> getById(@PathVariable UUID id) {
        GreenFencesParameterResponseDto response = greenFencesParameterService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Green Fences Parameters", description = "Retrieves all Green Fences Parameters")
    @GetMapping
    public ResponseEntity<List<GreenFencesParameterResponseDto>> getAll() {
        List<GreenFencesParameterResponseDto> responses = greenFencesParameterService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get latest active Green Fences Parameter", description = "Retrieves the most recently created active Green Fences Parameter")
    @GetMapping("/latest-active")
    public ResponseEntity<GreenFencesParameterResponseDto> getLatestActive() {
        GreenFencesParameterResponseDto response = greenFencesParameterService.getLatestActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Green Fences Parameter", description = "Updates an existing Green Fences Parameter with the provided data")
    @PutMapping("/{id}")
    public ResponseEntity<GreenFencesParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody GreenFencesParameterDto dto) {
        GreenFencesParameterResponseDto response = greenFencesParameterService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Disable Green Fences Parameter", description = "Disables a Green Fences Parameter by setting isActive to false (soft delete)")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        greenFencesParameterService.disable(id);
        return ResponseEntity.noContent().build();
    }
}

