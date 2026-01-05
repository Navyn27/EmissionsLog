package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service.ISWMMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/mitigation/iswm")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ISWMMitigationController {
    
    private final ISWMMitigationService service;
    
    @Operation(summary = "Create ISWM mitigation record", 
               description = "Creates a new Integrated Solid Waste Management (ISWM) mitigation project record. Calculates DOFDiverted, AvoidedLandfill, CompostingEmissions, NetAnnualReduction, MitigationScenarioEmission, and AdjustedBauEmissionMitigation based on input parameters and BAU.")
    @PostMapping
    public ResponseEntity<ApiResponse> createISWMMitigation(
            @Valid @RequestBody ISWMMitigationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "ISWM mitigation record created successfully", service.createISWMMitigation(dto)));
    }
    
    @Operation(summary = "Update ISWM mitigation record",
               description = "Updates an existing ISWM mitigation record and recalculates all derived fields (DOFDiverted, AvoidedLandfill, CompostingEmissions, NetAnnualReduction, MitigationScenarioEmission, AdjustedBauEmissionMitigation)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateISWMMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody ISWMMitigationDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "ISWM mitigation record updated successfully", service.updateISWMMitigation(id, dto)));
    }
    
    @Operation(summary = "Get ISWM mitigation records", 
               description = "Retrieves all Integrated Solid Waste Management mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllISWMMitigation(
            @RequestParam(required = false) Integer year) {
        List<ISWMMitigationResponseDto> mitigations = service.getAllISWMMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "ISWM mitigation records fetched successfully", mitigations));
    }
    
    @Operation(summary = "Delete ISWM mitigation record",
               description = "Deletes an existing ISWM mitigation record by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteISWMMitigation(@PathVariable UUID id) {
        service.deleteISWMMitigation(id);
        return ResponseEntity.ok(new ApiResponse(true, "ISWM mitigation record deleted successfully", null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download ISWM Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading ISWM Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "ISWM_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload ISWM Mitigation records from Excel file", description = "Uploads multiple ISWM Mitigation records from an Excel file.")
    public ResponseEntity<ApiResponse> createISWMMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createISWMMitigationFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int totalProcessed = (Integer) result.get("totalProcessed");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully out of %d processed.",
                savedCount,
                totalProcessed);

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }
}
