package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.controller;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models.ProtectiveForestMitigation;
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
        ProtectiveForestMitigation mitigation = service.createProtectiveForestMitigation(dto);
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
        ProtectiveForestMitigation mitigation = service.updateProtectiveForestMitigation(id, dto);
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
        List<ProtectiveForestMitigation> mitigations = 
            service.getAllProtectiveForestMitigation(year, category);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Protective forest mitigation records fetched successfully", 
            mitigations
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
        List<String> skippedRecords = (List<String>) result.get("skippedRecords");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped (year+category combinations already exist: %s)",
                savedCount,
                skippedCount,
                skippedRecords.isEmpty() ? "none" : String.join(", ", skippedRecords));

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }
}
