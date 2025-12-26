package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.service.StreetTreesParameterService;
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
@RequestMapping("/mitigation/streetTrees/parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Street Trees Parameters", description = "API endpoints for managing Street Trees Parameters")
public class StreetTreesParameterController {
    
    private final StreetTreesParameterService streetTreesParameterService;

    @Operation(summary = "Create a new Street Trees Parameter", description = "Creates a new Street Trees Parameter with the provided data")
    @PostMapping
    public ResponseEntity<StreetTreesParameterResponseDto> create(@Valid @RequestBody StreetTreesParameterDto dto) {
        StreetTreesParameterResponseDto response = streetTreesParameterService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Street Trees Parameter by ID", description = "Retrieves a Street Trees Parameter by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<StreetTreesParameterResponseDto> getById(@PathVariable UUID id) {
        StreetTreesParameterResponseDto response = streetTreesParameterService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Street Trees Parameters", description = "Retrieves all Street Trees Parameters")
    @GetMapping
    public ResponseEntity<List<StreetTreesParameterResponseDto>> getAll() {
        List<StreetTreesParameterResponseDto> responses = streetTreesParameterService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get latest active Street Trees Parameter", description = "Retrieves the most recently created active Street Trees Parameter")
    @GetMapping("/latest-active")
    public ResponseEntity<StreetTreesParameterResponseDto> getLatestActive() {
        StreetTreesParameterResponseDto response = streetTreesParameterService.getLatestActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Street Trees Parameter", description = "Updates an existing Street Trees Parameter with the provided data")
    @PutMapping("/{id}")
    public ResponseEntity<StreetTreesParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody StreetTreesParameterDto dto) {
        StreetTreesParameterResponseDto response = streetTreesParameterService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Disable Street Trees Parameter", description = "Disables a Street Trees Parameter by setting isActive to false (soft delete)")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        streetTreesParameterService.disable(id);
        return ResponseEntity.noContent().build();
    }
}

