package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.CreateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.UpdateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.model.LightBulb;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.service.ILightBulbService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/mitigation/lightBulb")
@SecurityRequirement(name = "BearerAuth")
@AllArgsConstructor
public class LightBulbController {
    private final ILightBulbService lightBulbService;

    @PostMapping
    @Operation(summary = "Create a new LightBulb entry")

    public ResponseEntity<LightBulb> create(@Valid @RequestBody CreateLightBulbDTO lightBulbDTO) {
        LightBulb createdLightBulb = lightBulbService.create(lightBulbDTO);
        return new ResponseEntity<>(createdLightBulb, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all LightBulb entries")
    public ResponseEntity<List<LightBulb>> getAll() {
        List<LightBulb> lightBulbs = lightBulbService.getAll();
        return new ResponseEntity<>(lightBulbs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a LightBulb entry by ID")

    public ResponseEntity<LightBulb> getById(@PathVariable UUID id) {
        LightBulb lightBulb = lightBulbService.getById(id);
        return new ResponseEntity<>(lightBulb, HttpStatus.OK);
    }

    @GetMapping("year/{year}")
    @Operation(summary = "Get a LightBulbs entry by year")
    public ResponseEntity<List<LightBulb>> getBYear(@PathVariable int year) {
        List<LightBulb> lightBulb = lightBulbService.getByYear(year);
        return new ResponseEntity<>(lightBulb, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a LightBulb entry")
    public ResponseEntity<LightBulb> update(@PathVariable UUID id,
            @Valid @RequestBody UpdateLightBulbDTO lightBulbDTO) {
        LightBulb updatedLightBulb = lightBulbService.update(id, lightBulbDTO);
        return new ResponseEntity<>(updatedLightBulb, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a LightBulb entry")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        lightBulbService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/template")
    @Operation(summary = "Download Light Bulb Mitigation Excel template", description = "Downloads an Excel template file with the required column headers for uploading Light Bulb Mitigation records.")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = lightBulbService.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Light_Bulb_Mitigation_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/excel")
    @Operation(summary = "Upload Light Bulb Mitigation records from Excel file", description = "Uploads multiple Light Bulb Mitigation records from an Excel file. Records with duplicate years will be skipped.")
    public ResponseEntity<ApiResponse> createLightBulbMitigationFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = lightBulbService.createLightBulbMitigationFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");
        @SuppressWarnings("unchecked")
        List<Integer> skippedYears = (List<Integer>) result.get("skippedYears");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped (year already exists: %s)",
                savedCount,
                skippedCount,
                skippedYears.isEmpty() ? "none"
                        : skippedYears.stream()
                                .map(String::valueOf)
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("none"));

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }
}
