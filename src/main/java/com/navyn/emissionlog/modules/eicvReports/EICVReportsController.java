package com.navyn.emissionlog.modules.eicvReports;

import com.navyn.emissionlog.modules.eicvReports.dtos.EICVReportDto;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/eicv")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class EICVReportsController {

    private final EICVReportService eicvReportService;

    @PostMapping
    public ResponseEntity<ApiResponse> createEICVReport(@RequestBody EICVReportDto eicvReportDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Report created successfully",
                eicvReportService.createEICVReport(eicvReportDto)));
    }

    @GetMapping("/year")
    public ResponseEntity<ApiResponse> getEICVReportByYear(@RequestParam("year") int year) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "EICV Report fetched successfully", eicvReportService.getEICVReportByYear(year)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllEICVReports(@RequestParam(required = false, value = "name") String name,
            @RequestParam(required = false, value = "year") Integer year) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "EICV Reports fetched successfully", eicvReportService.findAll(name, year)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEICVReportById(@PathVariable("id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "EICV Report fetched successfully", eicvReportService.getEICVReportById(id)));
    }

    @PostMapping("/excel")
    public ResponseEntity<ApiResponse> createEICVReportsFromExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Reports created successfully",
                eicvReportService.createReportsFromExcel(file)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEICVReport(@PathVariable("id") UUID eicvReportId,
            @RequestBody EICVReportDto eicvReportDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Report updated successfully",
                eicvReportService.updateEICVReport(eicvReportId, eicvReportDto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete EICV report record")
    public ResponseEntity<ApiResponse> deleteEICVReport(@PathVariable("id") UUID eicvReportId) {
        eicvReportService.deleteEICVReport(eicvReportId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "EICV Report deleted successfully", null));
    }

    @GetMapping("/template")
    @Operation(summary = "Download EICV Report Excel template", description = "Downloads an Excel template file with the required column headers for uploading EICV Reports")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] templateBytes = eicvReportService.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "EICV_Report_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }
}
