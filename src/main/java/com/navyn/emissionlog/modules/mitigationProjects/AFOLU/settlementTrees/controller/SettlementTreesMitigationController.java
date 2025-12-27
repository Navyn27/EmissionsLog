package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.controller;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.service.SettlementTreesMitigationService;
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
@RequestMapping("/mitigation/settlementTrees")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class SettlementTreesMitigationController {

        private final SettlementTreesMitigationService service;

        @PostMapping
        @Operation(summary = "Create new settlement trees mitigation record")
        public ResponseEntity<ApiResponse> createSettlementTreesMitigation(
                        @Valid @RequestBody SettlementTreesMitigationDto dto) {
                SettlementTreesMitigationResponseDto mitigation = service.createSettlementTreesMitigation(dto);
                return ResponseEntity.ok(new ApiResponse(
                                true,
                                "Settlement trees mitigation created successfully",
                                mitigation));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update settlement trees mitigation record")
        public ResponseEntity<ApiResponse> updateSettlementTreesMitigation(
                        @PathVariable UUID id,
                        @Valid @RequestBody SettlementTreesMitigationDto dto) {
                SettlementTreesMitigationResponseDto mitigation = service.updateSettlementTreesMitigation(id, dto);
                return ResponseEntity.ok(new ApiResponse(
                                true,
                                "Settlement trees mitigation updated successfully",
                                mitigation));
        }

        @GetMapping
        @Operation(summary = "Get all settlement trees mitigation records")
        public ResponseEntity<ApiResponse> getAllSettlementTreesMitigation(
                        @RequestParam(required = false, value = "year") Integer year) {
                List<SettlementTreesMitigationResponseDto> mitigations = service.getAllSettlementTreesMitigation(year);
                return ResponseEntity.ok(new ApiResponse(
                                true,
                                "Settlement trees mitigation records fetched successfully",
                                mitigations));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete settlement trees mitigation record")
        public ResponseEntity<ApiResponse> deleteSettlementTreesMitigation(@PathVariable UUID id) {
                service.deleteSettlementTreesMitigation(id);
                return ResponseEntity.ok(new ApiResponse(
                                true,
                                "Settlement trees mitigation deleted successfully",
                                null));
        }

        @GetMapping("/template")
        @Operation(summary = "Download Settlement Trees Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Settlement Trees Mitigation records")
        public ResponseEntity<byte[]> downloadExcelTemplate() {
                byte[] templateBytes = service.generateExcelTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "Settlement_Trees_Mitigation_Template.xlsx");
                headers.setContentLength(templateBytes.length);

                return ResponseEntity.ok()
                                .headers(headers)
                                .body(templateBytes);
        }

        @PostMapping("/excel")
        @Operation(summary = "Upload Settlement Trees Mitigation records from Excel file", description = "Uploads multiple Settlement Trees Mitigation records from an Excel file. Records with duplicate years will be skipped.")
        public ResponseEntity<ApiResponse> createSettlementTreesMitigationFromExcel(
                        @RequestParam("file") MultipartFile file) {
                Map<String, Object> result = service.createSettlementTreesMitigationFromExcel(file);

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
