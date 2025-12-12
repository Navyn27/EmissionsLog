package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.models.GreenFencesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.service.GreenFencesMitigationService;
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
@RequestMapping("/mitigation/greenFences")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class GreenFencesMitigationController {
    
    private final GreenFencesMitigationService service;
    
    @PostMapping
    @Operation(summary = "Create new green fences mitigation record")
    public ResponseEntity<ApiResponse> createGreenFencesMitigation(
            @Valid @RequestBody GreenFencesMitigationDto dto) {
        GreenFencesMitigation mitigation = service.createGreenFencesMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Green fences mitigation created successfully", 
            mitigation
        ));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update green fences mitigation record")
    public ResponseEntity<ApiResponse> updateGreenFencesMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody GreenFencesMitigationDto dto) {
        GreenFencesMitigation mitigation = service.updateGreenFencesMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Green fences mitigation updated successfully", 
            mitigation
        ));
    }
    
    @GetMapping
    @Operation(summary = "Get all green fences mitigation records")
    public ResponseEntity<ApiResponse> getAllGreenFencesMitigation(
            @RequestParam(required = false, value = "year") Integer year) {
        List<GreenFencesMitigation> mitigations = service.getAllGreenFencesMitigation(year);
        return ResponseEntity.ok(new ApiResponse(
            true, 
            "Green fences mitigation records fetched successfully", 
            mitigations
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete green fences mitigation record")
    public ResponseEntity<ApiResponse> deleteGreenFencesMitigation(@PathVariable UUID id) {
        service.deleteGreenFencesMitigation(id);
        return ResponseEntity.ok(new ApiResponse(
            true,
            "Green fences mitigation deleted successfully",
            null
        ));
    }

    @GetMapping("/template")
    @Operation(summary = "Download Green Fences Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Green Fences Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Green_Fences_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Green Fences Mitigation records from Excel file", description = "Uploads multiple Green Fences Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createGreenFencesMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createGreenFencesMitigationFromExcel(file);

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
