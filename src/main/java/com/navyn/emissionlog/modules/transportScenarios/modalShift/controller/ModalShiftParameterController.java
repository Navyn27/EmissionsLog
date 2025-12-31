package com.navyn.emissionlog.modules.transportScenarios.modalShift.controller;

import com.navyn.emissionlog.modules.transportScenarios.modalShift.dtos.ModalShiftParameterDto;
import com.navyn.emissionlog.modules.transportScenarios.modalShift.dtos.ModalShiftParameterResponseDto;
import com.navyn.emissionlog.modules.transportScenarios.modalShift.service.ModalShiftParameterService;
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
@RequestMapping("/mitigation/transport-scenarios/modal-shift/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ModalShiftParameterController {

    private final ModalShiftParameterService service;

    @Operation(summary = "Create Modal Shift Parameter",
            description = "Creates a new Modal Shift Parameter with energy content, emission factors, and GWP values")
    @PostMapping
    public ResponseEntity<ApiResponse> createModalShiftParameter(
            @Valid @RequestBody ModalShiftParameterDto dto) {
        ModalShiftParameterResponseDto parameter = service.createModalShiftParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Modal Shift Parameter created successfully", parameter));
    }

    @Operation(summary = "Update Modal Shift Parameter",
            description = "Updates an existing Modal Shift Parameter")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateModalShiftParameter(
            @PathVariable UUID id,
            @Valid @RequestBody ModalShiftParameterDto dto) {
        ModalShiftParameterResponseDto parameter = service.updateModalShiftParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Modal Shift Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get Modal Shift Parameter by ID",
            description = "Retrieves a specific Modal Shift Parameter by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getModalShiftParameterById(@PathVariable UUID id) {
        ModalShiftParameterResponseDto parameter = service.getModalShiftParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Modal Shift Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all Modal Shift Parameters",
            description = "Retrieves all Modal Shift Parameters, sorted with active ones first, then by creation date (newest first)")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllModalShiftParameters() {
        List<ModalShiftParameterResponseDto> parameters = service.getAllModalShiftParameters();
        return ResponseEntity.ok(new ApiResponse(true, "Modal Shift Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active Modal Shift Parameter",
            description = "Retrieves the most recently created active Modal Shift Parameter")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestActive() {
        ModalShiftParameterResponseDto parameter = service.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active Modal Shift Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable Modal Shift Parameter",
            description = "Disables an existing Modal Shift Parameter by setting isActive to false (soft delete)")
    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse> disableModalShiftParameter(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.ok(new ApiResponse(true, "Modal Shift Parameter disabled successfully", null));
    }

    @Operation(summary = "Delete Modal Shift Parameter",
            description = "Deletes an existing Modal Shift Parameter by its ID (permanent deletion)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteModalShiftParameter(@PathVariable UUID id) {
        service.deleteModalShiftParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "Modal Shift Parameter deleted successfully", null));
    }
}

