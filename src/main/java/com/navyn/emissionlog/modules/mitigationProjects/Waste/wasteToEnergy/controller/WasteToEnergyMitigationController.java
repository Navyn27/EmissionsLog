package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToEnergyMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToEnergyMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service.WasteToEnergyMitigationService;
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
@RequestMapping("/mitigation/wasteToEnergy")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class WasteToEnergyMitigationController {
    
    private final WasteToEnergyMitigationService service;
    
    @Operation(summary = "Create Waste-to-Energy mitigation record", 
               description = "Creates a new Waste-to-Energy mitigation project record with automatic GHG reduction calculations")
    @PostMapping
    public ResponseEntity<ApiResponse> createWasteToEnergyMitigation(
            @Valid @RequestBody WasteToEnergyMitigationDto dto) {
        WasteToEnergyMitigationResponseDto mitigation = service.createWasteToEnergyMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Waste-to-Energy mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Update Waste-to-Energy mitigation record",
               description = "Updates an existing Waste-to-Energy mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateWasteToEnergyMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody WasteToEnergyMitigationDto dto) {
        WasteToEnergyMitigationResponseDto mitigation = service.updateWasteToEnergyMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Waste-to-Energy mitigation record updated successfully", mitigation));
    }
    
    @Operation(summary = "Get Waste-to-Energy mitigation records", 
               description = "Retrieves all Waste-to-Energy mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllWasteToEnergyMitigation(
            @RequestParam(required = false) Integer year) {
        List<WasteToEnergyMitigationResponseDto> mitigations = service.getAllWasteToEnergyMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Waste-to-Energy mitigation records fetched successfully", mitigations));
    }
    
    @Operation(summary = "Delete Waste-to-Energy mitigation record",
               description = "Deletes an existing Waste-to-Energy mitigation record by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteWasteToEnergyMitigation(@PathVariable UUID id) {
        service.deleteWasteToEnergyMitigation(id);
        return ResponseEntity.ok(new ApiResponse(true, "Waste-to-Energy mitigation record deleted successfully", null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download Waste-to-Energy Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Waste-to-Energy Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Waste_to_Energy_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Waste-to-Energy Mitigation records from Excel file", description = "Uploads multiple Waste-to-Energy Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createWasteToEnergyMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createWasteToEnergyMitigationFromExcel(file);

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
