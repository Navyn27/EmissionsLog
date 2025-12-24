package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.service.CropRotationMitigationService;
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
@RequestMapping("/mitigation/cropRotation")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class CropRotationMitigationController {

    private final CropRotationMitigationService service;

    @PostMapping
    @Operation(summary = "Create new crop rotation mitigation record")
    public ResponseEntity<ApiResponse> createCropRotationMitigation(
            @Valid @RequestBody CropRotationMitigationDto dto) {
        CropRotationMitigationResponseDto mitigation = service.createCropRotationMitigation(dto);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Crop rotation mitigation created successfully",
                mitigation));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update crop rotation mitigation record")
    public ResponseEntity<ApiResponse> updateCropRotationMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody CropRotationMitigationDto dto) {
        CropRotationMitigationResponseDto mitigation = service.updateCropRotationMitigation(id, dto);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Crop rotation mitigation updated successfully",
                mitigation));
    }

    @GetMapping
    @Operation(summary = "Get all crop rotation mitigation records")
    public ResponseEntity<ApiResponse> getAllCropRotationMitigation(
            @RequestParam(required = false, value = "year") Integer year) {
        List<CropRotationMitigationResponseDto> mitigations = service.getAllCropRotationMitigation(year);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Crop rotation mitigation records fetched successfully",
                mitigations));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete crop rotation mitigation record")
    public ResponseEntity<ApiResponse> deleteCropRotationMitigation(@PathVariable UUID id) {
        service.deleteCropRotationMitigation(id);
        return ResponseEntity.ok(new ApiResponse(
                true,
                "Crop rotation mitigation deleted successfully",
                null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download Crop Rotation Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Crop Rotation Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Crop_Rotation_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Crop Rotation Mitigation records from Excel file", description = "Uploads multiple Crop Rotation Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createCropRotationMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createCropRotationMitigationFromExcel(file);

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
