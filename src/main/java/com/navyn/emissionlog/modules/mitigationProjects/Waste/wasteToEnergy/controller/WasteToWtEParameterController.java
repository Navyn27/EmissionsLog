package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToWtEParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToWtEParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service.WasteToWtEParameterService;
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
@RequestMapping("/mitigation/wasteToEnergy/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class WasteToWtEParameterController {

    private final WasteToWtEParameterService service;

    @Operation(summary = "Create Waste to WtE Parameter",
            description = "Creates a new Waste to WtE Parameter with net emission factor")
    @PostMapping
    public ResponseEntity<ApiResponse> createWasteToWtEParameter(
            @Valid @RequestBody WasteToWtEParameterDto dto) {
        WasteToWtEParameter parameter = service.createWasteToWtEParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Waste to WtE Parameter created successfully", parameter));
    }

    @Operation(summary = "Update Waste to WtE Parameter",
            description = "Updates an existing Waste to WtE Parameter")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateWasteToWtEParameter(
            @PathVariable UUID id,
            @Valid @RequestBody WasteToWtEParameterDto dto) {
        WasteToWtEParameter parameter = service.updateWasteToWtEParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Waste to WtE Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get Waste to WtE Parameter by ID",
            description = "Retrieves a specific Waste to WtE Parameter by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getWasteToWtEParameterById(@PathVariable UUID id) {
        WasteToWtEParameter parameter = service.getWasteToWtEParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Waste to WtE Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all Waste to WtE Parameters",
            description = "Retrieves all Waste to WtE Parameters ordered by creation date (newest first)")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllWasteToWtEParameters() {
        List<WasteToWtEParameter> parameters = service.getAllWasteToWtEParameters();
        return ResponseEntity.ok(new ApiResponse(true, "Waste to WtE Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest Waste to WtE Parameter",
            description = "Retrieves the most recently created Waste to WtE Parameter")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestWasteToWtEParameter() {
        WasteToWtEParameter parameter = service.getLatestWasteToWtEParameter();
        return ResponseEntity.ok(new ApiResponse(true, "Latest Waste to WtE Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Delete Waste to WtE Parameter",
            description = "Deletes an existing Waste to WtE Parameter by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteWasteToWtEParameter(@PathVariable UUID id) {
        service.deleteWasteToWtEParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "Waste to WtE Parameter deleted successfully", null));
    }
}

