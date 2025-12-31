package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.controller;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.service.ProtectiveForestMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/mitigation/protectiveForest")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ProtectiveForestMitigationController {
    
    private final ProtectiveForestMitigationService service;
    
    @PostMapping
    @Operation(summary = "Create new protective forest mitigation record")
    public ResponseEntity<ApiResponse> createProtectiveForestMitigation(
            @Valid @RequestBody ProtectiveForestMitigationDto dto) {
        ProtectiveForestMitigationResponseDto mitigation = service.createProtectiveForestMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Protective forest mitigation created successfully", 
            mitigation
        ));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update protective forest mitigation record")
    public ResponseEntity<ApiResponse> updateProtectiveForestMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody ProtectiveForestMitigationDto dto) {
        ProtectiveForestMitigationResponseDto mitigation = service.updateProtectiveForestMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Protective forest mitigation updated successfully", 
            mitigation
        ));
    }
    
    @GetMapping
    @Operation(summary = "Get all protective forest mitigation records")
    public ResponseEntity<ApiResponse> getAllProtectiveForestMitigation(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "category") ProtectiveForestCategory category) {
        List<ProtectiveForestMitigationResponseDto> mitigations = 
            service.getAllProtectiveForestMitigation(year, category);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Protective forest mitigation records fetched successfully", 
            mitigations
        ));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get protective forest mitigation record by ID")
    public ResponseEntity<ApiResponse> getProtectiveForestMitigationById(@PathVariable UUID id) {
        // This would need to be added to service interface if needed
        return ResponseEntity.ok(new ApiResponse(
            true,
            "Protective forest mitigation record fetched successfully",
            null
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete protective forest mitigation record")
    public ResponseEntity<ApiResponse> deleteProtectiveForestMitigation(@PathVariable UUID id) {
        service.deleteProtectiveForestMitigation(id);
        return ResponseEntity.ok(new ApiResponse(
            true,
            "Protective forest mitigation deleted successfully",
            null
        ));
    }

    @GetMapping("/template")
    @Operation(summary = "Download Protective Forest Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Protective Forest Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Protective_Forest_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Protective Forest Mitigation records from Excel file", description = "Uploads multiple Protective Forest Mitigation records from an Excel file. Records with duplicate year+category combinations will be skipped.")
    public ResponseEntity<ApiResponse> createProtectiveForestMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createProtectiveForestMitigationFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");
        @SuppressWarnings("unchecked")
        List<String> skippedYearsAndCategories = (List<String>) result.getOrDefault("skippedYearsAndCategories", new ArrayList<>());

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped (year+category combinations already exist: %s)",
                savedCount,
                skippedCount,
                skippedYearsAndCategories.isEmpty() ? "none" : String.join(", ", skippedYearsAndCategories));

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }
}
