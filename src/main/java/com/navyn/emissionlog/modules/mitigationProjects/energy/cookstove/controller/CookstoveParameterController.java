package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.*;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.parameters.*;
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
@RequestMapping("/mitigation/cookstove/parameters")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class CookstoveParameterController {

    private final IElectricityParameterService electricityService;
    private final ILGPParameterService lgpService;
    private final ICharcoalParameterService charcoalService;
    private final IFireWoodParameterService fireWoodService;

    // ==================== ELECTRICITY PARAMETERS ====================

    @Operation(summary = "Create Electricity Parameter", description = "Creates a new Electricity Parameter")
    @PostMapping("/electricity")
    public ResponseEntity<ApiResponse> createElectricityParameter(@Valid @RequestBody ElectricityParameterDto dto) {
        ElectricityParameterResponseDto parameter = electricityService.createParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Electricity Parameter created successfully", parameter));
    }

    @Operation(summary = "Update Electricity Parameter", description = "Updates an existing Electricity Parameter")
    @PutMapping("/electricity/{id}")
    public ResponseEntity<ApiResponse> updateElectricityParameter(
            @PathVariable UUID id,
            @Valid @RequestBody ElectricityParameterDto dto) {
        ElectricityParameterResponseDto parameter = electricityService.updateParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Electricity Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get Electricity Parameter by ID", description = "Retrieves a specific Electricity Parameter by its ID")
    @GetMapping("/electricity/{id}")
    public ResponseEntity<ApiResponse> getElectricityParameterById(@PathVariable UUID id) {
        ElectricityParameterResponseDto parameter = electricityService.getParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Electricity Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all Electricity Parameters", description = "Retrieves all Electricity Parameters, sorted with active ones first")
    @GetMapping("/electricity")
    public ResponseEntity<ApiResponse> getAllElectricityParameters() {
        List<ElectricityParameterResponseDto> parameters = electricityService.getAllParameters();
        return ResponseEntity.ok(new ApiResponse(true, "Electricity Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active Electricity Parameter", description = "Retrieves the most recently created active Electricity Parameter")
    @GetMapping("/electricity/latest-active")
    public ResponseEntity<ApiResponse> getLatestActiveElectricityParameter() {
        ElectricityParameterResponseDto parameter = electricityService.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active Electricity Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable Electricity Parameter", description = "Disables an Electricity Parameter by setting isActive to false")
    @PutMapping("/electricity/{id}/disable")
    public ResponseEntity<ApiResponse> disableElectricityParameter(@PathVariable UUID id) {
        electricityService.disableParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "Electricity Parameter disabled successfully", null));
    }

    @Operation(summary = "Delete Electricity Parameter", description = "Permanently deletes an Electricity Parameter")
    @DeleteMapping("/electricity/{id}")
    public ResponseEntity<ApiResponse> deleteElectricityParameter(@PathVariable UUID id) {
        electricityService.deleteParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "Electricity Parameter deleted successfully", null));
    }

    // ==================== LGP PARAMETERS ====================

    @Operation(summary = "Create LGP Parameter", description = "Creates a new LGP Parameter")
    @PostMapping("/lgp")
    public ResponseEntity<ApiResponse> createLGPParameter(@Valid @RequestBody LGPParameterDto dto) {
        LGPParameterResponseDto parameter = lgpService.createParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "LGP Parameter created successfully", parameter));
    }

    @Operation(summary = "Update LGP Parameter", description = "Updates an existing LGP Parameter")
    @PutMapping("/lgp/{id}")
    public ResponseEntity<ApiResponse> updateLGPParameter(
            @PathVariable UUID id,
            @Valid @RequestBody LGPParameterDto dto) {
        LGPParameterResponseDto parameter = lgpService.updateParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "LGP Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get LGP Parameter by ID", description = "Retrieves a specific LGP Parameter by its ID")
    @GetMapping("/lgp/{id}")
    public ResponseEntity<ApiResponse> getLGPParameterById(@PathVariable UUID id) {
        LGPParameterResponseDto parameter = lgpService.getParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "LGP Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all LGP Parameters", description = "Retrieves all LGP Parameters, sorted with active ones first")
    @GetMapping("/lgp")
    public ResponseEntity<ApiResponse> getAllLGPParameters() {
        List<LGPParameterResponseDto> parameters = lgpService.getAllParameters();
        return ResponseEntity.ok(new ApiResponse(true, "LGP Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active LGP Parameter", description = "Retrieves the most recently created active LGP Parameter")
    @GetMapping("/lgp/latest-active")
    public ResponseEntity<ApiResponse> getLatestActiveLGPParameter() {
        LGPParameterResponseDto parameter = lgpService.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active LGP Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable LGP Parameter", description = "Disables an LGP Parameter by setting isActive to false")
    @PutMapping("/lgp/{id}/disable")
    public ResponseEntity<ApiResponse> disableLGPParameter(@PathVariable UUID id) {
        lgpService.disableParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "LGP Parameter disabled successfully", null));
    }

    @Operation(summary = "Delete LGP Parameter", description = "Permanently deletes an LGP Parameter")
    @DeleteMapping("/lgp/{id}")
    public ResponseEntity<ApiResponse> deleteLGPParameter(@PathVariable UUID id) {
        lgpService.deleteParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "LGP Parameter deleted successfully", null));
    }

    // ==================== CHARCOAL PARAMETERS ====================

    @Operation(summary = "Create Charcoal Parameter", description = "Creates a new Charcoal Parameter")
    @PostMapping("/charcoal")
    public ResponseEntity<ApiResponse> createCharcoalParameter(@Valid @RequestBody CharcoalParameterDto dto) {
        CharcoalParameterResponseDto parameter = charcoalService.createParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Charcoal Parameter created successfully", parameter));
    }

    @Operation(summary = "Update Charcoal Parameter", description = "Updates an existing Charcoal Parameter")
    @PutMapping("/charcoal/{id}")
    public ResponseEntity<ApiResponse> updateCharcoalParameter(
            @PathVariable UUID id,
            @Valid @RequestBody CharcoalParameterDto dto) {
        CharcoalParameterResponseDto parameter = charcoalService.updateParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Charcoal Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get Charcoal Parameter by ID", description = "Retrieves a specific Charcoal Parameter by its ID")
    @GetMapping("/charcoal/{id}")
    public ResponseEntity<ApiResponse> getCharcoalParameterById(@PathVariable UUID id) {
        CharcoalParameterResponseDto parameter = charcoalService.getParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Charcoal Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all Charcoal Parameters", description = "Retrieves all Charcoal Parameters, sorted with active ones first")
    @GetMapping("/charcoal")
    public ResponseEntity<ApiResponse> getAllCharcoalParameters() {
        List<CharcoalParameterResponseDto> parameters = charcoalService.getAllParameters();
        return ResponseEntity.ok(new ApiResponse(true, "Charcoal Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active Charcoal Parameter", description = "Retrieves the most recently created active Charcoal Parameter")
    @GetMapping("/charcoal/latest-active")
    public ResponseEntity<ApiResponse> getLatestActiveCharcoalParameter() {
        CharcoalParameterResponseDto parameter = charcoalService.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active Charcoal Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable Charcoal Parameter", description = "Disables a Charcoal Parameter by setting isActive to false")
    @PutMapping("/charcoal/{id}/disable")
    public ResponseEntity<ApiResponse> disableCharcoalParameter(@PathVariable UUID id) {
        charcoalService.disableParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "Charcoal Parameter disabled successfully", null));
    }

    @Operation(summary = "Delete Charcoal Parameter", description = "Permanently deletes a Charcoal Parameter")
    @DeleteMapping("/charcoal/{id}")
    public ResponseEntity<ApiResponse> deleteCharcoalParameter(@PathVariable UUID id) {
        charcoalService.deleteParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "Charcoal Parameter deleted successfully", null));
    }

    // ==================== FIREWOOD PARAMETERS ====================

    @Operation(summary = "Create FireWood Parameter", description = "Creates a new FireWood Parameter")
    @PostMapping("/firewood")
    public ResponseEntity<ApiResponse> createFireWoodParameter(@Valid @RequestBody FireWoodParameterDto dto) {
        FireWoodParameterResponseDto parameter = fireWoodService.createParameter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "FireWood Parameter created successfully", parameter));
    }

    @Operation(summary = "Update FireWood Parameter", description = "Updates an existing FireWood Parameter")
    @PutMapping("/firewood/{id}")
    public ResponseEntity<ApiResponse> updateFireWoodParameter(
            @PathVariable UUID id,
            @Valid @RequestBody FireWoodParameterDto dto) {
        FireWoodParameterResponseDto parameter = fireWoodService.updateParameter(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "FireWood Parameter updated successfully", parameter));
    }

    @Operation(summary = "Get FireWood Parameter by ID", description = "Retrieves a specific FireWood Parameter by its ID")
    @GetMapping("/firewood/{id}")
    public ResponseEntity<ApiResponse> getFireWoodParameterById(@PathVariable UUID id) {
        FireWoodParameterResponseDto parameter = fireWoodService.getParameterById(id);
        return ResponseEntity.ok(new ApiResponse(true, "FireWood Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Get all FireWood Parameters", description = "Retrieves all FireWood Parameters, sorted with active ones first")
    @GetMapping("/firewood")
    public ResponseEntity<ApiResponse> getAllFireWoodParameters() {
        List<FireWoodParameterResponseDto> parameters = fireWoodService.getAllParameters();
        return ResponseEntity.ok(new ApiResponse(true, "FireWood Parameters fetched successfully", parameters));
    }

    @Operation(summary = "Get latest active FireWood Parameter", description = "Retrieves the most recently created active FireWood Parameter")
    @GetMapping("/firewood/latest-active")
    public ResponseEntity<ApiResponse> getLatestActiveFireWoodParameter() {
        FireWoodParameterResponseDto parameter = fireWoodService.getLatestActive();
        return ResponseEntity.ok(new ApiResponse(true, "Latest active FireWood Parameter fetched successfully", parameter));
    }

    @Operation(summary = "Disable FireWood Parameter", description = "Disables a FireWood Parameter by setting isActive to false")
    @PutMapping("/firewood/{id}/disable")
    public ResponseEntity<ApiResponse> disableFireWoodParameter(@PathVariable UUID id) {
        fireWoodService.disableParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "FireWood Parameter disabled successfully", null));
    }

    @Operation(summary = "Delete FireWood Parameter", description = "Permanently deletes a FireWood Parameter")
    @DeleteMapping("/firewood/{id}")
    public ResponseEntity<ApiResponse> deleteFireWoodParameter(@PathVariable UUID id) {
        fireWoodService.deleteParameter(id);
        return ResponseEntity.ok(new ApiResponse(true, "FireWood Parameter deleted successfully", null));
    }
}

