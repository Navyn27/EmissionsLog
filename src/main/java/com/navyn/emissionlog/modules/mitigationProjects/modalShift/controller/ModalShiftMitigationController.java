package com.navyn.emissionlog.modules.mitigationProjects.modalShift.controller;

import com.navyn.emissionlog.modules.mitigationProjects.modalShift.dtos.ModalShiftMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.modalShift.dtos.ModalShiftMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.modalShift.service.ModalShiftMitigationService;
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
@RequestMapping("/mitigation/transport-scenarios/modal-shift")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ModalShiftMitigationController {
    
    private final ModalShiftMitigationService service;
    
    @Operation(summary = "Create Modal Shift mitigation record", 
               description = "Creates a new Modal Shift mitigation project record. Requires an active Modal Shift Parameter.")
    @PostMapping
    public ResponseEntity<ApiResponse> createModalShiftMitigation(
            @Valid @RequestBody ModalShiftMitigationDto dto) {
        ModalShiftMitigationResponseDto mitigation = service.createModalShiftMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Modal Shift mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Update Modal Shift mitigation record",
               description = "Updates an existing Modal Shift mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateModalShiftMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody ModalShiftMitigationDto dto) {
        ModalShiftMitigationResponseDto mitigation = service.updateModalShiftMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Modal Shift mitigation record updated successfully", mitigation));
    }
    
    @Operation(summary = "Get Modal Shift mitigation records", 
               description = "Retrieves all Modal Shift mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllModalShiftMitigation(
            @RequestParam(required = false) Integer year) {
        List<ModalShiftMitigationResponseDto> mitigations = service.getAllModalShiftMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Modal Shift mitigation records fetched successfully", mitigations));
    }
    
    @Operation(summary = "Delete Modal Shift mitigation record",
               description = "Deletes an existing Modal Shift mitigation record by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteModalShiftMitigation(@PathVariable UUID id) {
        service.deleteModalShiftMitigation(id);
        return ResponseEntity.ok(new ApiResponse(true, "Modal Shift mitigation record deleted successfully", null));
    }
}

