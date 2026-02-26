package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.service.WetlandsRewettingParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mitigation/wetlandsRewetting/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
@Tag(name = "Wetlands Rewetting Parameters", description = "API for Wetlands Rewetting parameters (CH4 factor, GWP, sequestration factor)")
public class WetlandsRewettingParameterController {

    private final WetlandsRewettingParameterService parameterService;

    @PostMapping
    @Operation(summary = "Create a new Wetlands Rewetting parameter")
    public ResponseEntity<WetlandsRewettingParameterResponseDto> create(@Valid @RequestBody WetlandsRewettingParameterDto dto) {
        WetlandsRewettingParameterResponseDto response = parameterService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parameter by ID")
    public ResponseEntity<WetlandsRewettingParameterResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(parameterService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all parameters")
    public ResponseEntity<List<WetlandsRewettingParameterResponseDto>> getAll() {
        return ResponseEntity.ok(parameterService.getAll());
    }

    @GetMapping("/latest-active")
    @Operation(summary = "Get latest active parameter")
    public ResponseEntity<WetlandsRewettingParameterResponseDto> getLatestActive() {
        return ResponseEntity.ok(parameterService.getLatestActive());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update parameter")
    public ResponseEntity<WetlandsRewettingParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody WetlandsRewettingParameterDto dto) {
        return ResponseEntity.ok(parameterService.update(id, dto));
    }

    @PatchMapping("/{id}/disable")
    @Operation(summary = "Disable parameter (soft delete)")
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        parameterService.disable(id);
        return ResponseEntity.noContent().build();
    }
}
