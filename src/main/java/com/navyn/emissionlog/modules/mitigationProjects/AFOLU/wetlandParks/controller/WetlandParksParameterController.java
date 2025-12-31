package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.service.WetlandParksParameterService;
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
@RequestMapping("/mitigation/wetlandParks/parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Wetland Parks Parameters", description = "API endpoints for managing Wetland Parks Parameters")
public class WetlandParksParameterController {

    private final WetlandParksParameterService wetlandParksParameterService;

    @Operation(summary = "Create a new Wetland Parks Parameter", description = "Creates a new Wetland Parks Parameter with the provided data")
    @PostMapping
    public ResponseEntity<WetlandParksParameterResponseDto> create(
            @Valid @RequestBody WetlandParksParameterDto dto) {
        WetlandParksParameterResponseDto response = wetlandParksParameterService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Wetland Parks Parameter by ID", description = "Retrieves a Wetland Parks Parameter by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<WetlandParksParameterResponseDto> getById(@PathVariable UUID id) {
        WetlandParksParameterResponseDto response = wetlandParksParameterService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Wetland Parks Parameters", description = "Retrieves all Wetland Parks Parameters")
    @GetMapping
    public ResponseEntity<List<WetlandParksParameterResponseDto>> getAll() {
        List<WetlandParksParameterResponseDto> responses = wetlandParksParameterService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get latest active Wetland Parks Parameter", description = "Retrieves the most recently created active Wetland Parks Parameter")
    @GetMapping("/latest-active")
    public ResponseEntity<WetlandParksParameterResponseDto> getLatestActive() {
        WetlandParksParameterResponseDto response = wetlandParksParameterService.getLatestActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Wetland Parks Parameter", description = "Updates an existing Wetland Parks Parameter with the provided data")
    @PutMapping("/{id}")
    public ResponseEntity<WetlandParksParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody WetlandParksParameterDto dto) {
        WetlandParksParameterResponseDto response = wetlandParksParameterService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Disable Wetland Parks Parameter", description = "Disables a Wetland Parks Parameter by setting isActive to false (soft delete)")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        wetlandParksParameterService.disable(id);
        return ResponseEntity.noContent().build();
    }
}

