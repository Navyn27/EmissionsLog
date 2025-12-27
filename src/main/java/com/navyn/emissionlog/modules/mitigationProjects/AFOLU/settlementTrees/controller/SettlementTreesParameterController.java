package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.service.SettlementTreesParameterService;
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
@RequestMapping("/mitigation/settlementTrees/parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Settlement Trees Parameters", description = "API endpoints for managing Settlement Trees Parameters")
public class SettlementTreesParameterController {
    
    private final SettlementTreesParameterService settlementTreesParameterService;

    @Operation(summary = "Create a new Settlement Trees Parameter", description = "Creates a new Settlement Trees Parameter with the provided data")
    @PostMapping
    public ResponseEntity<SettlementTreesParameterResponseDto> create(@Valid @RequestBody SettlementTreesParameterDto dto) {
        SettlementTreesParameterResponseDto response = settlementTreesParameterService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Settlement Trees Parameter by ID", description = "Retrieves a Settlement Trees Parameter by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<SettlementTreesParameterResponseDto> getById(@PathVariable UUID id) {
        SettlementTreesParameterResponseDto response = settlementTreesParameterService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Settlement Trees Parameters", description = "Retrieves all Settlement Trees Parameters")
    @GetMapping
    public ResponseEntity<List<SettlementTreesParameterResponseDto>> getAll() {
        List<SettlementTreesParameterResponseDto> responses = settlementTreesParameterService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get latest active Settlement Trees Parameter", description = "Retrieves the most recently created active Settlement Trees Parameter")
    @GetMapping("/latest-active")
    public ResponseEntity<SettlementTreesParameterResponseDto> getLatestActive() {
        SettlementTreesParameterResponseDto response = settlementTreesParameterService.getLatestActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Settlement Trees Parameter", description = "Updates an existing Settlement Trees Parameter with the provided data")
    @PutMapping("/{id}")
    public ResponseEntity<SettlementTreesParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody SettlementTreesParameterDto dto) {
        SettlementTreesParameterResponseDto response = settlementTreesParameterService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Disable Settlement Trees Parameter", description = "Disables a Settlement Trees Parameter by setting isActive to false (soft delete)")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        settlementTreesParameterService.disable(id);
        return ResponseEntity.noContent().build();
    }
}

