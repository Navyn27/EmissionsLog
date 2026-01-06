package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.LightBulbParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.LightBulbParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.service.ILightBulbParameterService;
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
@RequestMapping("/mitigation/lightBulb/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class LightBulbParameterController {

    private final ILightBulbParameterService service;

    @Operation(summary = "Create Light Bulb Parameter",
            description = "Creates a new Light Bulb Parameter with net emission factor")
    @PostMapping
    public ResponseEntity<ApiResponse> createLightBulbEParameter(
            @Valid @RequestBody LightBulbParameterDto dto) {
        LightBulbParameterResponseDto parameter = service.createLightBulbParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Light Bulb Parameter created successfully", parameter));
    }

    @Operation(summary = "Update Light Bulb Parameter",
            description = "Updates an existing Light Bulb Parameter")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateLightBulbEParameter(
            @PathVariable UUID id,
            @Valid @RequestBody LightBulbParameterDto dto) {
        LightBulbParameterResponseDto parameter = service.updateLightBulbParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Light Bulb Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get Light Bulb Parameter by ID",
            description = "Retrieves a specific Light Bulb Parameter by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getLightBulbEParameterById(@PathVariable UUID id) {
        LightBulbParameterResponseDto parameter = service.getLightBulbParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Light Bulb Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all Light Bulb Parameters",
            description = "Retrieves all Light Bulb Parameters ordered by creation date (newest first)")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllLightBulbEParameters() {
        List<LightBulbParameterResponseDto> parameters = service.getAllLightBulbParameters();
        return ResponseEntity.ok(new ApiResponse(true, "Light Bulb Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active Light Bulb Parameter",
            description = "Retrieves the most recently created active Light Bulb Parameter")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestActive() {
        LightBulbParameterResponseDto parameter = service.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active Light Bulb Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable Light Bulb Parameter",
            description = "Disables an existing Light Bulb Parameter by setting isActive to false")
    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse> disableLightBulbEParameter(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.ok(new ApiResponse(true, "Light Bulb Parameter disabled successfully", null));
    }
    
}

