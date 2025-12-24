package com.navyn.emissionlog.modules.mitigationProjects.IPPU.controller;

import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUMitigationDTO;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUMitigationResponseDTO;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.model.IPPUMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.service.IIPPUService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@RequestMapping("/mitigation/ippu")
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "IPPU Mitigation Projects", description = "APIs for managing IPPU (Industrial Processes and Product Use) mitigation projects related to F-gases.")
public class IPPUController {
    private final IIPPUService iippuService;

    @Operation(summary = "Create a new IPPU mitigation entry",
            description = "Creates a new IPPU mitigation record, calculating emissions reductions based on the provided data.")

    @PostMapping
    public ResponseEntity<IPPUMitigation> create(@Valid @RequestBody IPPUMitigationDTO ippuMitigationDTO) {
        IPPUMitigation savedIPPUMitigation = iippuService.save(ippuMitigationDTO);
        return new ResponseEntity<>(savedIPPUMitigation, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all IPPU mitigation entries with totals",
            description = "Retrieves a list of all IPPU mitigation records, along with the sum of all 'mitigationScenario' and 'reducedEmissionInKtCO2e' values.")

    @GetMapping
    public ResponseEntity<IPPUMitigationResponseDTO> getAll() {
        IPPUMitigationResponseDTO response = iippuService.findAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get an IPPU mitigation entry by ID",
            description = "Retrieves a specific IPPU mitigation record by its unique ID.")

    @GetMapping("/{id}")
    public ResponseEntity<IPPUMitigation> getById(
            @Parameter(description = "Unique ID of the IPPU mitigation entry") @PathVariable UUID id) {
        return iippuService.findById(id)
                .map(ippuMitigation -> new ResponseEntity<>(ippuMitigation, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Get all IPPU mitigation entries for a specific year with totals",
            description = "Retrieves a list of all IPPU mitigation records for a given year, along with the sum of all 'mitigationScenario' and 'reducedEmissionInKtCO2e' values.")

    @GetMapping("/year/{year}")
    public ResponseEntity<IPPUMitigationResponseDTO> getByYear(
            @Parameter(description = "Year to filter IPPU mitigation entries") @PathVariable int year) {
        IPPUMitigationResponseDTO response = iippuService.findByYear(year);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update an IPPU mitigation entry",
            description = "Updates an existing IPPU mitigation record and recalculates the emissions values.")

    @PutMapping("/{id}")
    public ResponseEntity<IPPUMitigation> update(
            @Parameter(description = "Unique ID of the IPPU mitigation entry to update") @PathVariable UUID id,
            @Valid @RequestBody IPPUMitigationDTO ippuMitigationDTO) {
        IPPUMitigation updatedIPPUMitigation = iippuService.update(id, ippuMitigationDTO);
        return new ResponseEntity<>(updatedIPPUMitigation, HttpStatus.OK);
    }

    @Operation(summary = "Delete an IPPU mitigation entry",
            description = "Deletes an IPPU mitigation record by its unique ID.")

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Unique ID of the IPPU mitigation entry to delete") @PathVariable UUID id) {
        iippuService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/template")
    @Operation(summary = "Download IPPU Mitigation Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading IPPU Mitigation records")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = iippuService.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "IPPU_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload IPPU Mitigation records from Excel file", description = "Uploads multiple IPPU Mitigation records from an Excel file. Records with duplicate year+F-Gas Name combinations will be skipped.")
    public ResponseEntity<ApiResponse> createIPPUMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = iippuService.createIPPUMitigationFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");
        @SuppressWarnings("unchecked")
        List<String> skippedRecords = (List<String>) result.get("skippedRecords");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped (year+F-Gas Name already exist: %s)",
                savedCount,
                skippedCount,
                skippedRecords.isEmpty() ? "none" : String.join(", ", skippedRecords));

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }
}
