package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.service.DailySpreadMitigationService;
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
@RequestMapping("/mitigation/daily-spread")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class DailySpreadMitigationController {
    
    private final DailySpreadMitigationService service;
    
    @Operation(summary = "Create daily spread MMS mitigation record", 
               description = "Creates a new Daily Spread MMS mitigation record for CH4 reduction")
    @PostMapping
    public ResponseEntity<ApiResponse> createDailySpreadMitigation(
            @Valid @RequestBody DailySpreadMitigationDto dto) {
        DailySpreadMitigationResponseDto mitigation = service.createDailySpreadMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Daily spread mitigation record created successfully", mitigation));
    }

    @Operation(summary = "Update daily spread MMS mitigation record",
               description = "Updates an existing Daily Spread MMS mitigation record for CH4 reduction")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateDailySpreadMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody DailySpreadMitigationDto dto) {
        DailySpreadMitigationResponseDto mitigation = service.updateDailySpreadMitigation(id, dto);
        return ResponseEntity.ok(
                new ApiResponse(true, "Daily spread mitigation record updated successfully", mitigation)
        );
    }
    
    @Operation(summary = "Get daily spread MMS mitigation records", 
               description = "Retrieves all Daily Spread MMS mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllDailySpreadMitigation(
            @RequestParam(required = false) Integer year) {
        List<DailySpreadMitigationResponseDto> mitigations = service.getAllDailySpreadMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Daily spread mitigation records fetched successfully", mitigations));
    }

    @Operation(summary = "Delete daily spread MMS mitigation record",
               description = "Deletes a Daily Spread MMS mitigation record by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDailySpreadMitigation(@PathVariable UUID id) {
        service.deleteDailySpreadMitigation(id);
        return ResponseEntity.ok(
                new ApiResponse(true, "Daily spread mitigation record deleted successfully", null)
        );
    }

    @GetMapping("/template")
    @Operation(summary = "Download Daily Spread Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Daily Spread Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Daily_Spread_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Daily Spread Mitigation records from Excel file", description = "Uploads multiple Daily Spread Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createDailySpreadMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createDailySpreadMitigationFromExcel(file);

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
