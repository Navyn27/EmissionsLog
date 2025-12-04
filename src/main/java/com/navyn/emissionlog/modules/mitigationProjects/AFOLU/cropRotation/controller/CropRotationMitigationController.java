package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models.CropRotationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.service.CropRotationMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mitigation/cropRotation")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class CropRotationMitigationController {
    
    private final CropRotationMitigationService service;
    
    @PostMapping
    @Operation(summary = "Create new crop rotation mitigation record")
    public ResponseEntity<ApiResponse> createCropRotationMitigation(
            @Valid @RequestBody CropRotationMitigationDto dto) {
        CropRotationMitigation mitigation = service.createCropRotationMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Crop rotation mitigation created successfully", 
            mitigation
        ));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update crop rotation mitigation record")
    public ResponseEntity<ApiResponse> updateCropRotationMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody CropRotationMitigationDto dto) {
        CropRotationMitigation mitigation = service.updateCropRotationMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Crop rotation mitigation updated successfully", 
            mitigation
        ));
    }
    
    @GetMapping
    @Operation(summary = "Get all crop rotation mitigation records")
    public ResponseEntity<ApiResponse> getAllCropRotationMitigation(
            @RequestParam(required = false, value = "year") Integer year) {
        List<CropRotationMitigation> mitigations = service.getAllCropRotationMitigation(year);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Crop rotation mitigation records fetched successfully", 
            mitigations
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete crop rotation mitigation record")
    public ResponseEntity<ApiResponse> deleteCropRotationMitigation(@PathVariable UUID id) {
        service.deleteCropRotationMitigation(id);
        return ResponseEntity.ok(new ApiResponse(
            true,
            "Crop rotation mitigation deleted successfully",
            null
        ));
    }
}
