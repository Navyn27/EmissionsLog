package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.service.AddingStrawParameterService;
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
@RequestMapping("/mitigation/addingStraw/parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Adding Straw Parameters", description = "API endpoints for managing Adding Straw Parameters")
public class AddingStrawParameterController {

    private final AddingStrawParameterService addingStrawParameterService;

    @Operation(summary = "Create a new Adding Straw Parameter", description = "Creates a new Adding Straw Parameter with the provided data")
    @PostMapping
    public ResponseEntity<AddingStrawParameterResponseDto> create(
            @Valid @RequestBody AddingStrawParameterDto dto) {
        AddingStrawParameterResponseDto response = addingStrawParameterService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Adding Straw Parameter by ID", description = "Retrieves an Adding Straw Parameter by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<AddingStrawParameterResponseDto> getById(@PathVariable UUID id) {
        AddingStrawParameterResponseDto response = addingStrawParameterService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Adding Straw Parameters", description = "Retrieves all Adding Straw Parameters")
    @GetMapping
    public ResponseEntity<List<AddingStrawParameterResponseDto>> getAll() {
        List<AddingStrawParameterResponseDto> responses = addingStrawParameterService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get latest active Adding Straw Parameter", description = "Retrieves the most recently created active Adding Straw Parameter")
    @GetMapping("/latest-active")
    public ResponseEntity<AddingStrawParameterResponseDto> getLatestActive() {
        AddingStrawParameterResponseDto response = addingStrawParameterService.getLatestActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Adding Straw Parameter", description = "Updates an existing Adding Straw Parameter with the provided data")
    @PutMapping("/{id}")
    public ResponseEntity<AddingStrawParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody AddingStrawParameterDto dto) {
        AddingStrawParameterResponseDto response = addingStrawParameterService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Disable Adding Straw Parameter", description = "Disables an Adding Straw Parameter by setting isActive to false (soft delete)")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        addingStrawParameterService.disable(id);
        return ResponseEntity.noContent().build();
    }
}

