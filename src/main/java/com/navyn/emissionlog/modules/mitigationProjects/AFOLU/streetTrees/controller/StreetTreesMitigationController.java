package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.service.StreetTreesMitigationService;
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
@RequestMapping("/mitigation/streetTrees")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class StreetTreesMitigationController {

    private final StreetTreesMitigationService service;

    @PostMapping
    @Operation(summary = "Create new street trees mitigation record")
    public ResponseEntity<ApiResponse> createStreetTreesMitigation(
            @Valid @RequestBody StreetTreesMitigationDto dto) {
        StreetTreesMitigationResponseDto mitigation = service.createStreetTreesMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Street trees mitigation created successfully",
                mitigation));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update street trees mitigation record")
    public ResponseEntity<ApiResponse> updateStreetTreesMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody StreetTreesMitigationDto dto) {
        StreetTreesMitigationResponseDto mitigation = service.updateStreetTreesMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Street trees mitigation updated successfully",
                mitigation));
    }

    @GetMapping
    @Operation(summary = "Get all street trees mitigation records")
    public ResponseEntity<ApiResponse> getAllStreetTreesMitigation(
            @RequestParam(required = false, value = "year") Integer year) {
        List<StreetTreesMitigationResponseDto> mitigations = service.getAllStreetTreesMitigation(year);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Street trees mitigation records fetched successfully",
                mitigations));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete street trees mitigation record")
    public ResponseEntity<ApiResponse> deleteStreetTreesMitigation(@PathVariable UUID id) {
        service.deleteStreetTreesMitigation(id);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Street trees mitigation deleted successfully",
                null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download Street Trees Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Street Trees Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Street_Trees_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Street Trees Mitigation records from Excel file", description = "Uploads multiple Street Trees Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createStreetTreesMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createStreetTreesMitigationFromExcel(file);

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
