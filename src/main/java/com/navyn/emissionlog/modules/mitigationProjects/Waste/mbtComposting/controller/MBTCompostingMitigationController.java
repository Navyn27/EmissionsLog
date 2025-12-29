package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.service.MBTCompostingMitigationService;
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
@RequestMapping("/mitigation/mbtComposting")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class MBTCompostingMitigationController {
    
    private final MBTCompostingMitigationService service;
    
    @Operation(summary = "Create MBT/Aerobic Composting mitigation record", 
               description = "Creates a new Mechanical and Biological Treatment/Aerobic Composting mitigation project record with automatic calculations based on operation status")
    @PostMapping
    public ResponseEntity<ApiResponse> createMBTCompostingMitigation(
            @Valid @RequestBody MBTCompostingMitigationDto dto) {
        MBTCompostingMitigationResponseDto mitigation = service.createMBTCompostingMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "MBT/Aerobic Composting mitigation record created successfully", mitigation));
    }
    
    @Operation(summary = "Update MBT/Aerobic Composting mitigation record",
               description = "Updates an existing MBT/Aerobic Composting mitigation record and recalculates all derived fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateMBTCompostingMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody MBTCompostingMitigationDto dto) {
        MBTCompostingMitigationResponseDto mitigation = service.updateMBTCompostingMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "MBT/Aerobic Composting mitigation record updated successfully", mitigation));
    }
    
    @Operation(summary = "Get MBT/Aerobic Composting mitigation records", 
               description = "Retrieves all MBT/Aerobic Composting mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllMBTCompostingMitigation(
            @RequestParam(required = false) Integer year) {
        List<MBTCompostingMitigationResponseDto> mitigations = service.getAllMBTCompostingMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "MBT/Aerobic Composting mitigation records fetched successfully", mitigations));
    }
    
    @Operation(summary = "Delete MBT/Aerobic Composting mitigation record",
               description = "Deletes an existing MBT/Aerobic Composting mitigation record by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMBTCompostingMitigation(@PathVariable UUID id) {
        service.deleteMBTCompostingMitigation(id);
        return ResponseEntity.ok(new ApiResponse(true, "MBT/Aerobic Composting mitigation record deleted successfully", null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download MBT Composting Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading MBT Composting Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "MBT_Composting_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload MBT Composting Mitigation records from Excel file", description = "Uploads multiple MBT Composting Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createMBTCompostingMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createMBTCompostingMitigationFromExcel(file);

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
