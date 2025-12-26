package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasUtilizationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasUtilizationMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service.LandfillGasUtilizationMitigationService;
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
@RequestMapping("/mitigation/landfillGasUtilization")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class LandfillGasUtilizationMitigationController {

    private final LandfillGasUtilizationMitigationService service;

    @Operation(summary = "Create Landfill Gas Utilization mitigation record",
            description = "Creates a new Landfill Gas Utilization mitigation project record for Kigali City with automatic calculations (project active after 2028)")
    @PostMapping
    public ResponseEntity<ApiResponse> createLandfillGasUtilizationMitigation(
            @Valid @RequestBody LandfillGasUtilizationMitigationDto dto) {
        LandfillGasUtilizationMitigationResponseDto mitigation = service.createLandfillGasUtilizationMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Landfill Gas Utilization mitigation record created successfully", mitigation));
    }

    @Operation(summary = "Update Landfill Gas Utilization mitigation record",
            description = "Updates an existing Landfill Gas Utilization mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateLandfillGasUtilizationMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody LandfillGasUtilizationMitigationDto dto) {
        LandfillGasUtilizationMitigationResponseDto mitigation = service.updateLandfillGasUtilizationMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Landfill Gas Utilization mitigation record updated successfully", mitigation));
    }

    @Operation(summary = "Get Landfill Gas Utilization mitigation records",
            description = "Retrieves all Landfill Gas Utilization mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllLandfillGasUtilizationMitigation(
            @RequestParam(required = false) Integer year) {
        List<LandfillGasUtilizationMitigationResponseDto> mitigations = service.getAllLandfillGasUtilizationMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Landfill Gas Utilization mitigation records fetched successfully", mitigations));
    }

    @Operation(summary = "Delete Landfill Gas Utilization mitigation record",
            description = "Deletes an existing Landfill Gas Utilization mitigation record by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteLandfillGasUtilizationMitigation(@PathVariable UUID id) {
        service.deleteLandfillGasUtilizationMitigation(id);
        return ResponseEntity.ok(new ApiResponse(true, "Landfill Gas Utilization mitigation record deleted successfully", null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download Landfill Gas Utilization Mitigation Excel template",
            description = "Downloads an Excel template file with the required column headers and data validation for uploading Landfill Gas Utilization Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Landfill_Gas_Utilization_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Landfill Gas Utilization Mitigation records from Excel file",
            description = "Uploads multiple Landfill Gas Utilization Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createLandfillGasUtilizationMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createLandfillGasUtilizationMitigationFromExcel(file);

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
