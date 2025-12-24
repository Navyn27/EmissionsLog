package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveInstallationDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigationYear;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.StoveMitigationService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
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
public class StoveMitigationController {

    private final StoveMitigationService mitigationService;

    public StoveMitigationController(StoveMitigationService mitigationService) {
        this.mitigationService = mitigationService;
    }

    @PostMapping
    public StoveMitigationYear create(@RequestBody StoveInstallationDTO request) {
        return mitigationService.createMitigation(request);
    }

    @GetMapping
    public List<StoveMitigationYear> getAll() {
        return mitigationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoveMitigationYear> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mitigationService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoveMitigationYear> updateById(@PathVariable UUID id,
            @RequestBody StoveInstallationDTO request) {
        return ResponseEntity.ok(mitigationService.updateById(id, request));
    }

    @GetMapping("/stove-type/{stoveTypeId}")
    public List<StoveMitigationYear> getByStoveType(@PathVariable UUID stoveTypeId) {
        return mitigationService.findByStoveType(stoveTypeId);
    }

    @GetMapping("/year/{year}")
    public List<StoveMitigationYear> getByYear(@PathVariable int year) {
        return mitigationService.findByYear(year);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        mitigationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/template")
    @Operation(summary = "Download Cookstove Mitigation Excel template", description = "Downloads an Excel template file with the required column headers for uploading Cookstove Mitigation records. Users can type stove type names, and new stove types will be created automatically.")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = mitigationService.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Cookstove_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Cookstove Mitigation records from Excel file", description = "Uploads multiple Cookstove Mitigation records from an Excel file. Stove types will be found (case-insensitive) or created automatically. Records with duplicate year+stove type combinations will be skipped.")
    public ResponseEntity<ApiResponse> createCookstoveMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = mitigationService.createCookstoveMitigationFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");
        @SuppressWarnings("unchecked")
        List<String> skippedRecords = (List<String>) result.get("skippedRecords");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped (year+stove type already exist: %s)",
                savedCount,
                skippedCount,
                skippedRecords.isEmpty() ? "none" : String.join(", ", skippedRecords));

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }
}
