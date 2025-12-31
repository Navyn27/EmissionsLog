package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.controller;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.service.WetlandParksMitigationService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/mitigation/wetlandParks")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class WetlandParksMitigationController {

    private final WetlandParksMitigationService service;

    @PostMapping
    @Operation(summary = "Create new wetland parks mitigation record")
    public ResponseEntity<ApiResponse> createWetlandParksMitigation(
            @Valid @RequestBody WetlandParksMitigationDto dto) {
        WetlandParksMitigationResponseDto mitigation = service.createWetlandParksMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Wetland parks mitigation created successfully",
                mitigation));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update wetland parks mitigation record")
    public ResponseEntity<ApiResponse> updateWetlandParksMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody WetlandParksMitigationDto dto) {
        WetlandParksMitigationResponseDto mitigation = service.updateWetlandParksMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Wetland parks mitigation updated successfully",
                mitigation));
    }

    @GetMapping
    @Operation(summary = "Get all wetland parks mitigation records")
    public ResponseEntity<ApiResponse> getAllWetlandParksMitigation(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "category") WetlandTreeCategory category) {
        List<WetlandParksMitigationResponseDto> mitigations = service.getAllWetlandParksMitigation(year, category);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Wetland parks mitigation records fetched successfully",
                mitigations));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete wetland parks mitigation record")
    public ResponseEntity<ApiResponse> deleteWetlandParksMitigation(@PathVariable UUID id) {
        service.deleteWetlandParksMitigation(id);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Wetland parks mitigation deleted successfully",
                null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download Wetland Parks Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Wetland Parks Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Wetland_Parks_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Wetland Parks Mitigation records from Excel file", description = "Uploads multiple Wetland Parks Mitigation records from an Excel file. Records with duplicate year+treeCategory combinations will be skipped.")
    public ResponseEntity<ApiResponse> createWetlandParksMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createWetlandParksMitigationFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");
        @SuppressWarnings("unchecked")
        List<String> skippedYearsAndCategories = (List<String>) result.getOrDefault("skippedYearsAndCategories", new ArrayList<>());

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped (year+treeCategory combinations already exist: %s)",
                savedCount,
                skippedCount,
                skippedYearsAndCategories.isEmpty() ? "none" : String.join(", ", skippedYearsAndCategories));

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }
}
