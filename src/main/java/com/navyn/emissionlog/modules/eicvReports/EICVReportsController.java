package com.navyn.emissionlog.modules.eicvReports;

import com.navyn.emissionlog.modules.eicvReports.dtos.EICVReportDto;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
         return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Report created successfully", eicvReportService.createEICVReport(eicvReportDto)));
     }

     @GetMapping("/year")
     public ResponseEntity<ApiResponse> getEICVReportByYear(@RequestParam("year") int year) {
         return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Report fetched successfully", eicvReportService.getEICVReportByYear(year)));
     }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllEICVReports(){
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Reports fetched successfully", eicvReportService.findAll()));}

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEICVReportById(@PathVariable("id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Report fetched successfully", eicvReportService.getEICVReportById(id)));
    }

    @PostMapping("/excel")
    public ResponseEntity<ApiResponse> createEICVReportsFromExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Reports created successfully", eicvReportService.createReportsFromExcel(file)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEICVReport(@PathVariable("id") UUID eicvReportId, @RequestBody EICVReportDto eicvReportDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Report updated successfully", eicvReportService.updateEICVReport(eicvReportId, eicvReportDto)));
    }
}
