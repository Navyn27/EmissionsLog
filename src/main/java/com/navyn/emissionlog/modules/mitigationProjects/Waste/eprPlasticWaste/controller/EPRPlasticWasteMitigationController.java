package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRPlasticWasteMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRPlasticWasteMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.service.EPRPlasticWasteMitigationService;
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
@RequestMapping("/mitigation/eprPlasticWaste")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class EPRPlasticWasteMitigationController {
    
    private final EPRPlasticWasteMitigationService service;
    
    @Operation(summary = "Create EPR Circular Economy Plastic Waste mitigation record", 
               description = "Creates a new EPR Plastic Waste mitigation project record. Requires an active EPR Parameter and a BAU record for the Waste sector and same year.")
    @PostMapping
    public ResponseEntity<ApiResponse> createEPRPlasticWasteMitigation(
            @Valid @RequestBody EPRPlasticWasteMitigationDto dto) {
        EPRPlasticWasteMitigationResponseDto mitigation = service.createEPRPlasticWasteMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "EPR Plastic Waste mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Update EPR Plastic Waste mitigation record",
               description = "Updates an existing EPR Plastic Waste mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEPRPlasticWasteMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody EPRPlasticWasteMitigationDto dto) {
        EPRPlasticWasteMitigationResponseDto mitigation = service.updateEPRPlasticWasteMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "EPR Plastic Waste mitigation record updated successfully", mitigation));
    }
    
    @Operation(summary = "Get EPR Plastic Waste mitigation records", 
               description = "Retrieves all EPR Plastic Waste mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllEPRPlasticWasteMitigation(
            @RequestParam(required = false) Integer year) {
        List<EPRPlasticWasteMitigationResponseDto> mitigations = service.getAllEPRPlasticWasteMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "EPR Plastic Waste mitigation records fetched successfully", mitigations));
    }
    
    @Operation(summary = "Delete EPR Plastic Waste mitigation record",
               description = "Deletes an existing EPR Plastic Waste mitigation record by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEPRPlasticWasteMitigation(@PathVariable UUID id) {
        service.deleteEPRPlasticWasteMitigation(id);
        return ResponseEntity.ok(new ApiResponse(true, "EPR Plastic Waste mitigation record deleted successfully", null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download EPR Plastic Waste Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading EPR Plastic Waste Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "EPR_Plastic_Waste_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload EPR Plastic Waste Mitigation records from Excel file", description = "Uploads multiple EPR Plastic Waste Mitigation records from an Excel file. Records with duplicate years will be skipped. Requires an active EPR Parameter and BAU records for each year.")
    public ResponseEntity<ApiResponse> createEPRPlasticWasteMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createEPRPlasticWasteMitigationFromExcel(file);

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
