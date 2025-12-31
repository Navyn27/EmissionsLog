package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.service.DailySpreadParameterService;
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
@RequestMapping("/mitigation/dailySpread/parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Daily Spread Parameters", description = "API endpoints for managing Daily Spread Parameters")
public class DailySpreadParameterController {

    private final DailySpreadParameterService service;

    @Operation(summary = "Create a new Daily Spread Parameter", description = "Creates a new Daily Spread Parameter with the provided data")
    @PostMapping
    public ResponseEntity<DailySpreadParameterResponseDto> create(
            @Valid @RequestBody DailySpreadParameterDto dto) {
        DailySpreadParameterResponseDto response = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Daily Spread Parameter by ID", description = "Retrieves a Daily Spread Parameter by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<DailySpreadParameterResponseDto> getById(@PathVariable UUID id) {
        DailySpreadParameterResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Daily Spread Parameters", description = "Retrieves all Daily Spread Parameters")
    @GetMapping
    public ResponseEntity<List<DailySpreadParameterResponseDto>> getAll() {
        List<DailySpreadParameterResponseDto> responses = service.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get latest active Daily Spread Parameter", description = "Retrieves the most recently created active Daily Spread Parameter")
    @GetMapping("/latest-active")
    public ResponseEntity<DailySpreadParameterResponseDto> getLatestActive() {
        DailySpreadParameterResponseDto response = service.getLatestActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Daily Spread Parameter", description = "Updates an existing Daily Spread Parameter with the provided data")
    @PutMapping("/{id}")
    public ResponseEntity<DailySpreadParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody DailySpreadParameterDto dto) {
        DailySpreadParameterResponseDto response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Disable Daily Spread Parameter", description = "Disables a Daily Spread Parameter by setting isActive to false (soft delete)")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.noContent().build();
    }
}

