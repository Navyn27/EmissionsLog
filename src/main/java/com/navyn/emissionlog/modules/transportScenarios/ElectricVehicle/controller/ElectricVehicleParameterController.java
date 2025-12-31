package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.controller;

import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleParameterDto;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleParameterResponseDto;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.service.ElectricVehicleParameterService;
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
@RequestMapping("/mitigation/transport-scenarios/electric-vehicle/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ElectricVehicleParameterController {

    private final ElectricVehicleParameterService service;

    @Operation(summary = "Create Electric Vehicle Parameter",
            description = "Creates a new Electric Vehicle Parameter with grid emission factor")
    @PostMapping
    public ResponseEntity<ApiResponse> createElectricVehicleParameter(
            @Valid @RequestBody ElectricVehicleParameterDto dto) {
        ElectricVehicleParameterResponseDto parameter = service.createElectricVehicleParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Electric Vehicle Parameter created successfully", parameter));
    }

    @Operation(summary = "Update Electric Vehicle Parameter",
            description = "Updates an existing Electric Vehicle Parameter")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateElectricVehicleParameter(
            @PathVariable UUID id,
            @Valid @RequestBody ElectricVehicleParameterDto dto) {
        ElectricVehicleParameterResponseDto parameter = service.updateElectricVehicleParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Electric Vehicle Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get Electric Vehicle Parameter by ID",
            description = "Retrieves a specific Electric Vehicle Parameter by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getElectricVehicleParameterById(@PathVariable UUID id) {
        ElectricVehicleParameterResponseDto parameter = service.getElectricVehicleParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Electric Vehicle Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all Electric Vehicle Parameters",
            description = "Retrieves all Electric Vehicle Parameters, sorted with active ones first, then by creation date (newest first)")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllElectricVehicleParameters() {
        List<ElectricVehicleParameterResponseDto> parameters = service.getAllElectricVehicleParameters();
        return ResponseEntity.ok(new ApiResponse(true, "Electric Vehicle Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active Electric Vehicle Parameter",
            description = "Retrieves the most recently created active Electric Vehicle Parameter")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestActive() {
        ElectricVehicleParameterResponseDto parameter = service.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active Electric Vehicle Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable Electric Vehicle Parameter",
            description = "Disables an existing Electric Vehicle Parameter by setting isActive to false (soft delete)")
    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse> disableElectricVehicleParameter(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.ok(new ApiResponse(true, "Electric Vehicle Parameter disabled successfully", null));
    }
}

