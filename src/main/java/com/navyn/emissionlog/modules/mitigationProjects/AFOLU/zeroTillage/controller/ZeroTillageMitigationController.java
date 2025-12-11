package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.service.ZeroTillageMitigationService;
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
@RequestMapping("/mitigation/zeroTillage")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class ZeroTillageMitigationController {

    private final ZeroTillageMitigationService service;

    @PostMapping
    @Operation(summary = "Create new zero tillage mitigation record")
    public ResponseEntity<ApiResponse> createZeroTillageMitigation(
            @Valid @RequestBody ZeroTillageMitigationDto dto) {
        ZeroTillageMitigation mitigation = service.createZeroTillageMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Zero tillage mitigation created successfully",
                mitigation));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update zero tillage mitigation record")
    public ResponseEntity<ApiResponse> updateZeroTillageMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody ZeroTillageMitigationDto dto) {
        ZeroTillageMitigation mitigation = service.updateZeroTillageMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Zero tillage mitigation updated successfully",
                mitigation));
    }

    @GetMapping
    @Operation(summary = "Get all zero tillage mitigation records")
    public ResponseEntity<ApiResponse> getAllZeroTillageMitigation(
            @RequestParam(required = false, value = "year") Integer year) {
        List<ZeroTillageMitigation> mitigations = service.getAllZeroTillageMitigation(year);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Zero tillage mitigation records fetched successfully",
                mitigations));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete zero tillage mitigation record")
    public ResponseEntity<ApiResponse> deleteZeroTillageMitigation(@PathVariable UUID id) {
        service.deleteZeroTillageMitigation(id);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Zero tillage mitigation deleted successfully",
                null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download Zero Tillage Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Zero Tillage Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Zero_Tillage_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Zero Tillage Mitigation records from Excel file", description = "Uploads multiple Zero Tillage Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createZeroTillageMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createZeroTillageMitigationFromExcel(file);

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
