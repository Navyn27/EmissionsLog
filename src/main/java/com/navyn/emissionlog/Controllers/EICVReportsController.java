package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Payload.Requests.EICVReportDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.EICVReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/eicv")
public class EICVReportsController {

    @Autowired
    private EICVReportService eicvReportService;

     @PostMapping
     public ResponseEntity<ApiResponse> createEICVReport(@RequestBody EICVReportDto eicvReportDto) {
         return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Report created successfully", eicvReportService.createEICVReport(eicvReportDto)));
     }

     @GetMapping("/{year}")
     public ResponseEntity<ApiResponse> getEICVReportByYear(@RequestParam int year) {
         return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Report fetched successfully", eicvReportService.getEICVReportByYear(year)));
     }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllEICVReports(){
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Reports fetched successfully", eicvReportService.findAll()));}

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEICVReportById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Report fetched successfully", eicvReportService.getEICVReportById(id)));
    }

    @PostMapping("/excel")
    public ResponseEntity<ApiResponse> createEICVReportsFromExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "EICV Reports created successfully", eicvReportService.createReportsFromExcel(file)));
    }

}
