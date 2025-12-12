package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service.ManureCoveringMitigationService;
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
@RequestMapping("/mitigation/manure-covering")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ManureCoveringMitigationController {
    
    private final ManureCoveringMitigationService service;
    
    @Operation(summary = "Create manure covering mitigation record", 
               description = "Creates a new Manure Covering (Compaction and Manure Covering) mitigation record for N2O reduction")
    @PostMapping
    public ResponseEntity<ApiResponse> createManureCoveringMitigation(
            @Valid @RequestBody ManureCoveringMitigationDto dto) {
        ManureCoveringMitigation mitigation = service.createManureCoveringMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Manure covering mitigation record created successfully", mitigation));
    }

    @Operation(summary = "Update manure covering mitigation record",
               description = "Updates an existing Manure Covering mitigation record for N2O reduction")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateManureCoveringMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody ManureCoveringMitigationDto dto) {
        ManureCoveringMitigation mitigation = service.updateManureCoveringMitigation(id, dto);
        return ResponseEntity.ok(
                new ApiResponse(true, "Manure covering mitigation record updated successfully", mitigation)
        );
    }
    
    @Operation(summary = "Get manure covering mitigation records", 
               description = "Retrieves all Manure Covering mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllManureCoveringMitigation(
            @RequestParam(required = false) Integer year) {
        List<ManureCoveringMitigation> mitigations = service.getAllManureCoveringMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Manure covering mitigation records fetched successfully", mitigations));
    }

    @Operation(summary = "Delete manure covering mitigation record",
               description = "Deletes a Manure Covering mitigation record by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteManureCoveringMitigation(@PathVariable UUID id) {
        service.deleteManureCoveringMitigation(id);
        return ResponseEntity.ok(
                new ApiResponse(true, "Manure covering mitigation record deleted successfully", null)
        );
    }

    @GetMapping("/template")
    @Operation(summary = "Download Manure Covering Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Manure Covering Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Manure_Covering_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Manure Covering Mitigation records from Excel file", description = "Uploads multiple Manure Covering Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createManureCoveringMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createManureCoveringMitigationFromExcel(file);

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
