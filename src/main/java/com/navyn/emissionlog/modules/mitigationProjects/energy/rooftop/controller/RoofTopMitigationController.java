package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.service.IRoofTopMitigationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/mitigation/rooftops")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
public class RoofTopMitigationController {
    private final IRoofTopMitigationService roofTopMitigationService;

    @PostMapping
    public ResponseEntity<RoofTopMitigationResponseDto> create(@Valid @RequestBody RoofTopMitigationDto dto) {
        RoofTopMitigationResponseDto response = roofTopMitigationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoofTopMitigationResponseDto> getById(@PathVariable UUID id) {
        RoofTopMitigationResponseDto response = roofTopMitigationService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RoofTopMitigationResponseDto>> getAll() {
        List<RoofTopMitigationResponseDto> responses = roofTopMitigationService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<RoofTopMitigationResponseDto>> getByYear(@PathVariable int year) {
        List<RoofTopMitigationResponseDto> responses = roofTopMitigationService.getByYear(year);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoofTopMitigationResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody RoofTopMitigationDto dto) {
        RoofTopMitigationResponseDto response = roofTopMitigationService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roofTopMitigationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/template")
    @io.swagger.v3.oas.annotations.Operation(summary = "Download Rooftop Mitigation Excel template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] bytes = roofTopMitigationService.generateExcelTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "rooftop_mitigation_template.xlsx");
        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @PostMapping("/excel")
    @io.swagger.v3.oas.annotations.Operation(summary = "Upload Rooftop Mitigation records from Excel")
    public ResponseEntity<com.navyn.emissionlog.utils.ApiResponse> createFromExcel(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        java.util.Map<String, Object> result = roofTopMitigationService.createFromExcel(file);
        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");
        String message = String.format("Upload completed. %d record(s) saved successfully. %d record(s) skipped.", savedCount, skippedCount);
        return ResponseEntity.ok(new com.navyn.emissionlog.utils.ApiResponse(true, message, result));
    }
}
