package com.navyn.emissionlog.modules.transportScenarios.controllers;

import com.navyn.emissionlog.modules.transportScenarios.dtos.*;
import com.navyn.emissionlog.modules.transportScenarios.services.TransportScenarioService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transport/scenarios")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Transport Scenarios", description = "Transport BAU & Mitigation Scenarios API")
public class TransportScenarioController {

    private final TransportScenarioService service;

    public TransportScenarioController(TransportScenarioService service) {
        this.service = service;
    }

    // ==================== Scenario CRUD Endpoints ====================

    @PostMapping
    @Operation(summary = "Create a new transport scenario")
    public ResponseEntity<ApiResponse> createScenario(
            @Valid @RequestBody TransportScenarioCreateDto dto) {
        TransportScenarioResponseDto result = service.createScenario(dto);
        return ResponseEntity.ok(new ApiResponse(true, "Scenario created successfully", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing transport scenario")
    public ResponseEntity<ApiResponse> updateScenario(
            @PathVariable UUID id,
            @Valid @RequestBody TransportScenarioCreateDto dto) {
        TransportScenarioResponseDto result = service.updateScenario(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Scenario updated successfully", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transport scenario")
    public ResponseEntity<ApiResponse> deleteScenario(@PathVariable UUID id) {
        service.deleteScenario(id);
        return ResponseEntity.ok(new ApiResponse(true, "Scenario deleted successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a transport scenario by ID")
    public ResponseEntity<ApiResponse> getScenario(@PathVariable UUID id) {
        TransportScenarioResponseDto result = service.getScenario(id);
        return ResponseEntity.ok(new ApiResponse(true, "Scenario retrieved successfully", result));
    }

    @GetMapping
    @Operation(summary = "Get all transport scenarios")
    public ResponseEntity<ApiResponse> getAllScenarios() {
        List<TransportScenarioResponseDto> result = service.getAllScenarios();
        return ResponseEntity.ok(new ApiResponse(true, "Scenarios retrieved successfully", result));
    }

    // ==================== Vehicle Assumptions Endpoints ====================

    @PostMapping("/vehicle-assumptions")
    @Operation(summary = "Create or update a vehicle assumption")
    public ResponseEntity<ApiResponse> createOrUpdateVehicleAssumption(
            @Valid @RequestBody TransportScenarioVehicleAssumptionDto dto) {
        TransportScenarioVehicleAssumptionDto result = service.createOrUpdateVehicleAssumption(dto);
        return ResponseEntity.ok(new ApiResponse(true, "Vehicle assumption saved successfully", result));
    }

    @GetMapping("/{scenarioId}/vehicle-assumptions")
    @Operation(summary = "Get all vehicle assumptions for a scenario")
    public ResponseEntity<ApiResponse> getVehicleAssumptionsForScenario(
            @PathVariable UUID scenarioId) {
        List<TransportScenarioVehicleAssumptionDto> result = service.getVehicleAssumptionsForScenario(scenarioId);
        return ResponseEntity.ok(new ApiResponse(true, "Vehicle assumptions retrieved successfully", result));
    }

    @DeleteMapping("/vehicle-assumptions/{assumptionId}")
    @Operation(summary = "Delete a vehicle assumption")
    public ResponseEntity<ApiResponse> deleteVehicleAssumption(@PathVariable UUID assumptionId) {
        service.deleteVehicleAssumption(assumptionId);
        return ResponseEntity.ok(new ApiResponse(true, "Vehicle assumption deleted successfully"));
    }

    // ==================== Global Assumptions Endpoints ====================

    @PostMapping("/global-assumptions")
    @Operation(summary = "Create or update a global assumption for a year")
    public ResponseEntity<ApiResponse> createOrUpdateGlobalAssumption(
            @Valid @RequestBody TransportScenarioYearGlobalAssumptionDto dto) {
        TransportScenarioYearGlobalAssumptionDto result = service.createOrUpdateGlobalAssumption(dto);
        return ResponseEntity.ok(new ApiResponse(true, "Global assumption saved successfully", result));
    }

    @GetMapping("/{scenarioId}/global-assumptions")
    @Operation(summary = "Get all global assumptions for a scenario")
    public ResponseEntity<ApiResponse> getGlobalAssumptionsForScenario(
            @PathVariable UUID scenarioId) {
        List<TransportScenarioYearGlobalAssumptionDto> result = service.getGlobalAssumptionsForScenario(scenarioId);
        return ResponseEntity.ok(new ApiResponse(true, "Global assumptions retrieved successfully", result));
    }

    @DeleteMapping("/global-assumptions/{assumptionId}")
    @Operation(summary = "Delete a global assumption")
    public ResponseEntity<ApiResponse> deleteGlobalAssumption(@PathVariable UUID assumptionId) {
        service.deleteGlobalAssumption(assumptionId);
        return ResponseEntity.ok(new ApiResponse(true, "Global assumption deleted successfully"));
    }

    // ==================== Modal Shift Assumptions Endpoints ====================

    @PostMapping("/modal-shift-assumptions")
    @Operation(summary = "Create or update a modal shift assumption")
    public ResponseEntity<ApiResponse> createOrUpdateModalShiftAssumption(
            @Valid @RequestBody TransportScenarioModalShiftAssumptionDto dto) {
        TransportScenarioModalShiftAssumptionDto result = service.createOrUpdateModalShiftAssumption(dto);
        return ResponseEntity.ok(new ApiResponse(true, "Modal shift assumption saved successfully", result));
    }

    @GetMapping("/{scenarioId}/modal-shift-assumptions")
    @Operation(summary = "Get all modal shift assumptions for a scenario")
    public ResponseEntity<ApiResponse> getModalShiftAssumptionsForScenario(
            @PathVariable UUID scenarioId) {
        List<TransportScenarioModalShiftAssumptionDto> result = service.getModalShiftAssumptionsForScenario(scenarioId);
        return ResponseEntity.ok(new ApiResponse(true, "Modal shift assumptions retrieved successfully", result));
    }

    @DeleteMapping("/modal-shift-assumptions/{assumptionId}")
    @Operation(summary = "Delete a modal shift assumption")
    public ResponseEntity<ApiResponse> deleteModalShiftAssumption(@PathVariable UUID assumptionId) {
        service.deleteModalShiftAssumption(assumptionId);
        return ResponseEntity.ok(new ApiResponse(true, "Modal shift assumption deleted successfully"));
    }

    // ==================== Scenario Execution Endpoint ====================

    @GetMapping("/{scenarioId}/run")
    @Operation(summary = "Run the scenario and get BAU vs mitigation results")
    public ResponseEntity<ApiResponse> runScenario(@PathVariable UUID scenarioId) {
        TransportScenarioRunResponseDto result = service.runScenario(scenarioId);
        return ResponseEntity.ok(new ApiResponse(true, "Scenario executed successfully", result));
    }
}
