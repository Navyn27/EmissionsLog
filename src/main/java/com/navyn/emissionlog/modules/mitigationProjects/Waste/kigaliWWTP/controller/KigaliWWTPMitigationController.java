package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants.WWTPProjectPhase;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service.KigaliWWTPMitigationService;
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
@RequestMapping("/mitigation/kigaliWWTP")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class KigaliWWTPMitigationController {
    
    private final KigaliWWTPMitigationService service;
    
    @Operation(summary = "Create Kigali WWTP mitigation record", 
               description = "Creates a new Kigali Wastewater Treatment Plant (WWTP) mitigation project record at GITICYINYONI with year-based household connection rates")
    @PostMapping
    public ResponseEntity<ApiResponse> createKigaliWWTPMitigation(
            @Valid @RequestBody KigaliWWTPMitigationDto dto) {
        KigaliWWTPMitigation mitigation = service.createKigaliWWTPMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Kigali WWTP mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Update Kigali WWTP mitigation record",
               description = "Updates an existing Kigali WWTP mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateKigaliWWTPMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody KigaliWWTPMitigationDto dto) {
        KigaliWWTPMitigation mitigation = service.updateKigaliWWTPMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP mitigation record updated successfully", mitigation));
    }
    
    @Operation(summary = "Get Kigali WWTP mitigation records", 
               description = "Retrieves all Kigali WWTP mitigation records with optional year and project phase filters")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllKigaliWWTPMitigation(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) WWTPProjectPhase projectPhase) {
        List<KigaliWWTPMitigation> mitigations = service.getAllKigaliWWTPMitigation(year, projectPhase);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP mitigation records fetched successfully", mitigations));
    }
    
    @Operation(summary = "Delete Kigali WWTP mitigation record",
               description = "Deletes an existing Kigali WWTP mitigation record by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteKigaliWWTPMitigation(@PathVariable UUID id) {
        service.deleteKigaliWWTPMitigation(id);
        return ResponseEntity.ok(new ApiResponse(true, "Kigali WWTP mitigation record deleted successfully", null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download Kigali WWTP Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Kigali WWTP Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Kigali_WWTP_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Kigali WWTP Mitigation records from Excel file", description = "Uploads multiple Kigali WWTP Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createKigaliWWTPMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createKigaliWWTPMitigationFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");
        @SuppressWarnings("unchecked")
        List<Integer> skippedYears = (List<Integer>) result.get("skippedYears");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped (years already exist: %s)",
                savedCount,
                skippedCount,
                skippedYears.isEmpty() ? "none" : skippedYears.toString());

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }
}
