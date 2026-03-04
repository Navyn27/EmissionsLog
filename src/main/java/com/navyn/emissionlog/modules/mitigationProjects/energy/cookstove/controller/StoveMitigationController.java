package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.CreateStoveMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.StoveMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.UpdateStoveMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.enums.EStoveType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.IStoveMitigationService;
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
@RequestMapping("/mitigation/cookstoves")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class StoveMitigationController {

    private final IStoveMitigationService service;

    @Operation(summary = "Create Stove Mitigation record")
    @PostMapping
    public ResponseEntity<ApiResponse> createStoveMitigation(
            @Valid @RequestBody CreateStoveMitigationDto dto) {
        StoveMitigationResponseDto mitigation = service.createStoveMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Stove Mitigation record created successfully", mitigation));
    }

    @Operation(summary = "Update Stove Mitigation record")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateStoveMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStoveMitigationDto dto) {
        StoveMitigationResponseDto mitigation = service.updateStoveMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Stove Mitigation record updated successfully", mitigation));
    }

    @Operation(summary = "Get Stove Mitigation record by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getStoveMitigationById(@PathVariable UUID id) {
        StoveMitigationResponseDto mitigation = service.getStoveMitigationById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Stove Mitigation record fetched successfully", mitigation));
    }

    @Operation(summary = "Get all Stove Mitigation records")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllStoveMitigations(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) EStoveType stoveType) {
        List<StoveMitigationResponseDto> mitigations = service.getAllStoveMitigations(year, stoveType);
        return ResponseEntity.ok(new ApiResponse(true, "Stove Mitigation records fetched successfully", mitigations));
    }

    @Operation(summary = "Delete Stove Mitigation record")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteStoveMitigation(@PathVariable UUID id) {
        service.deleteStoveMitigation(id);
        return ResponseEntity.ok(new ApiResponse(true, "Stove Mitigation record deleted successfully", null));
    }

    @Operation(summary = "Download Cookstove Mitigation Excel template", description = "Downloads an Excel template file with the required column headers for uploading Cookstove Mitigation records.")
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Cookstove_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @Operation(summary = "Upload Cookstove Mitigation records from Excel file", description = "Uploads multiple Cookstove Mitigation records from an Excel file. Records with errors will be skipped.")
    @PostMapping("/excel")
    public ResponseEntity<ApiResponse> createStoveMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createStoveMitigationFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped.",
                savedCount,
                skippedCount);

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }
}

