package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.service.CropRotationParameterService;
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
@RequestMapping("/mitigation/cropRotation/parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Crop Rotation Parameters", description = "API endpoints for managing Crop Rotation Parameters")
public class CropRotationParameterController {
    
    private final CropRotationParameterService cropRotationParameterService;

    @Operation(summary = "Create a new Crop Rotation Parameter", description = "Creates a new Crop Rotation Parameter with the provided data")
    @PostMapping
    public ResponseEntity<CropRotationParameterResponseDto> create(@Valid @RequestBody CropRotationParameterDto dto) {
        CropRotationParameterResponseDto response = cropRotationParameterService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Crop Rotation Parameter by ID", description = "Retrieves a Crop Rotation Parameter by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<CropRotationParameterResponseDto> getById(@PathVariable UUID id) {
        CropRotationParameterResponseDto response = cropRotationParameterService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Crop Rotation Parameters", description = "Retrieves all Crop Rotation Parameters")
    @GetMapping
    public ResponseEntity<List<CropRotationParameterResponseDto>> getAll() {
        List<CropRotationParameterResponseDto> responses = cropRotationParameterService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get latest active Crop Rotation Parameter", description = "Retrieves the most recently created active Crop Rotation Parameter")
    @GetMapping("/latest-active")
    public ResponseEntity<CropRotationParameterResponseDto> getLatestActive() {
        CropRotationParameterResponseDto response = cropRotationParameterService.getLatestActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Crop Rotation Parameter", description = "Updates an existing Crop Rotation Parameter with the provided data")
    @PutMapping("/{id}")
    public ResponseEntity<CropRotationParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody CropRotationParameterDto dto) {
        CropRotationParameterResponseDto response = cropRotationParameterService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Disable Crop Rotation Parameter", description = "Disables a Crop Rotation Parameter by setting isActive to false (soft delete)")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        cropRotationParameterService.disable(id);
        return ResponseEntity.noContent().build();
    }
}

