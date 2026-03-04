package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.service.WetlandsRewettingMitigationService;
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
@RequestMapping("/mitigation/wetlandsRewetting")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class WetlandsRewettingMitigationController {

    private final WetlandsRewettingMitigationService service;

    @PostMapping
    @Operation(summary = "Create Wetlands Rewetting mitigation record")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody WetlandsRewettingMitigationDto dto) {
        WetlandsRewettingMitigationResponseDto mitigation = service.create(dto);
        return ResponseEntity.ok(new ApiResponse(true, "Wetlands Rewetting mitigation created successfully", mitigation));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Wetlands Rewetting mitigation record")
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @Valid @RequestBody WetlandsRewettingMitigationDto dto) {
        WetlandsRewettingMitigationResponseDto mitigation = service.update(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Wetlands Rewetting mitigation updated successfully", mitigation));
    }

    @GetMapping
    @Operation(summary = "Get all Wetlands Rewetting mitigation records")
    public ResponseEntity<ApiResponse> getAll(@RequestParam(required = false) Integer year) {
        List<WetlandsRewettingMitigationResponseDto> mitigations = service.getAll(year);
        return ResponseEntity.ok(new ApiResponse(true, "Wetlands Rewetting mitigation records fetched successfully", mitigations));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Wetlands Rewetting mitigation by ID")
    public ResponseEntity<ApiResponse> getById(@PathVariable UUID id) {
        return service.getById(id)
                .map(m -> ResponseEntity.ok(new ApiResponse(true, "Wetlands Rewetting mitigation fetched successfully", m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Wetlands Rewetting mitigation record")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(new ApiResponse(true, "Wetlands Rewetting mitigation deleted successfully", null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download Wetlands Rewetting Excel template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] templateBytes = service.generateExcelTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Wetlands_Rewetting_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);
        return ResponseEntity.ok().headers(headers).body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Wetlands Rewetting records from Excel")
    public ResponseEntity<ApiResponse> createFromExcel(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = service.createFromExcel(file);
        return ResponseEntity.ok(new ApiResponse(true, "Upload completed", result));
    }
}
