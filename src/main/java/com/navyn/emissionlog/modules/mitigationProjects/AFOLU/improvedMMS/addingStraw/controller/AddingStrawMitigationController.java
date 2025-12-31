package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.service.AddingStrawMitigationService;
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
@RequestMapping("/mitigation/adding-straw")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class AddingStrawMitigationController {
    
    private final AddingStrawMitigationService service;
    
    @Operation(summary = "Create adding straw mitigation record", 
               description = "Creates a new Adding Straw to Cow Dung mitigation record for CH4 reduction")
    @PostMapping
    public ResponseEntity<ApiResponse> createAddingStrawMitigation(
            @Valid @RequestBody AddingStrawMitigationDto dto) {
        AddingStrawMitigationResponseDto mitigation = service.createAddingStrawMitigation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, "Adding straw mitigation record created successfully", mitigation));
    }

    @Operation(summary = "Update adding straw mitigation record",
               description = "Updates an existing Adding Straw mitigation record")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAddingStrawMitigation(
            @PathVariable UUID id,
            @Valid @RequestBody AddingStrawMitigationDto dto) {
        AddingStrawMitigationResponseDto mitigation = service.updateAddingStrawMitigation(id, dto);
        return ResponseEntity.ok(
                new ApiResponse(true, "Adding straw mitigation record updated successfully", mitigation)
        );
    }
    
    @Operation(summary = "Get adding straw mitigation records", 
               description = "Retrieves all Adding Straw mitigation records with optional year filter")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllAddingStrawMitigation(
            @RequestParam(required = false) Integer year) {
        List<AddingStrawMitigationResponseDto> mitigations = service.getAllAddingStrawMitigation(year);
        return ResponseEntity.ok(new ApiResponse(true, "Adding straw mitigation records fetched successfully", mitigations));
    }

    @Operation(summary = "Delete adding straw mitigation record",
               description = "Deletes an Adding Straw mitigation record by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAddingStrawMitigation(@PathVariable UUID id) {
        service.deleteAddingStrawMitigation(id);
        return ResponseEntity.ok(
                new ApiResponse(true, "Adding straw mitigation record deleted successfully", null)
        );
    }

    @GetMapping("/template")
    @Operation(summary = "Download Adding Straw Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Adding Straw Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Adding_Straw_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Adding Straw Mitigation records from Excel file", description = "Uploads multiple Adding Straw Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createAddingStrawMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createAddingStrawMitigationFromExcel(file);

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
