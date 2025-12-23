package com.navyn.emissionlog.modules.intervention.controllers;

import com.navyn.emissionlog.modules.intervention.Intervention;
import com.navyn.emissionlog.modules.intervention.dtos.InterventionDto;
import com.navyn.emissionlog.modules.intervention.services.InterventionService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/interventions")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class InterventionController {

    private final InterventionService interventionService;

    @Operation(summary = "Create a new intervention", description = "Creates a new intervention with the provided details.")
    @PostMapping
    public ResponseEntity<ApiResponse> createIntervention(@Valid @RequestBody InterventionDto dto) {
        Intervention intervention = interventionService.createIntervention(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Intervention created successfully", intervention));
    }

    @Operation(summary = "Get intervention by ID", description = "Fetches an intervention identified by the provided ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getInterventionById(@PathVariable("id") UUID id) {
        return interventionService.getInterventionById(id)
                .map(intervention -> ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, "Intervention fetched successfully", intervention)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Intervention not found with id: " + id, null)));
    }

    @Operation(summary = "Get all interventions", description = "Fetches all interventions available in the system.")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllInterventions() {
        List<Intervention> interventions = interventionService.getAllInterventions();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "Interventions fetched successfully", interventions));
    }

    @Operation(summary = "Update an intervention", description = "Updates the intervention identified by the provided ID with the new details.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateIntervention(
            @PathVariable("id") UUID id,
            @Valid @RequestBody InterventionDto dto) {
        Intervention intervention = interventionService.updateIntervention(id, dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "Intervention updated successfully", intervention));
    }

    @Operation(summary = "Delete an intervention", description = "Deletes the intervention identified by the provided ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteIntervention(@PathVariable("id") UUID id) {
        interventionService.deleteIntervention(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "Intervention deleted successfully", null));
    }
}

