package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.service.ZeroTillageParameterService;
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
@RequestMapping("/mitigation/zeroTillage/parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Zero Tillage Parameters", description = "API endpoints for managing Zero Tillage Parameters")
public class ZeroTillageParameterController {
    
    private final ZeroTillageParameterService zeroTillageParameterService;

    @Operation(summary = "Create a new Zero Tillage Parameter", description = "Creates a new Zero Tillage Parameter with the provided data")
    @PostMapping
    public ResponseEntity<ZeroTillageParameterResponseDto> create(@Valid @RequestBody ZeroTillageParameterDto dto) {
        ZeroTillageParameterResponseDto response = zeroTillageParameterService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Zero Tillage Parameter by ID", description = "Retrieves a Zero Tillage Parameter by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<ZeroTillageParameterResponseDto> getById(@PathVariable UUID id) {
        ZeroTillageParameterResponseDto response = zeroTillageParameterService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Zero Tillage Parameters", description = "Retrieves all Zero Tillage Parameters")
    @GetMapping
    public ResponseEntity<List<ZeroTillageParameterResponseDto>> getAll() {
        List<ZeroTillageParameterResponseDto> responses = zeroTillageParameterService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get latest active Zero Tillage Parameter", description = "Retrieves the most recently created active Zero Tillage Parameter")
    @GetMapping("/latest-active")
    public ResponseEntity<ZeroTillageParameterResponseDto> getLatestActive() {
        ZeroTillageParameterResponseDto response = zeroTillageParameterService.getLatestActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Zero Tillage Parameter", description = "Updates an existing Zero Tillage Parameter with the provided data")
    @PutMapping("/{id}")
    public ResponseEntity<ZeroTillageParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody ZeroTillageParameterDto dto) {
        ZeroTillageParameterResponseDto response = zeroTillageParameterService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Disable Zero Tillage Parameter", description = "Disables a Zero Tillage Parameter by setting isActive to false (soft delete)")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        zeroTillageParameterService.disable(id);
        return ResponseEntity.noContent().build();
    }
}

