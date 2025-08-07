package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Enums.Countries;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.PopulationRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.navyn.emissionlog.Payload.Requests.CreatePopulationRecordDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/population")
@SecurityRequirement(name = "BearerAuth")
public class PopulationRecordsController {
     @Autowired
     private PopulationRecordService populationRecordService;

     @Operation(summary = "Create Population Record", description = "Create a new population record")
     @PostMapping
     public ResponseEntity<ApiResponse> createPopulationRecord(@RequestBody CreatePopulationRecordDto createPopulationRecordDto) {
         return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse( true, "Population Records saved successfully", populationRecordService.createPopulationRecord(createPopulationRecordDto)));
     }

     @Operation(summary = "Get All Population Records", description = "Get all population records")
     @GetMapping
     public ResponseEntity<ApiResponse> getAllPopulationRecords() {
         return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Population Records fetched successfully", populationRecordService.getAllPopulationRecords()));
     }

     @Operation(summary = "Get Population Record By ID", description = "Get a population record by ID")
     @GetMapping("/{id}")
     public ResponseEntity<ApiResponse> getPopulationRecordById(@PathVariable("id") UUID id) {
         return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Population Record fetched successfully", populationRecordService.getPopulationRecordById(id)));
     }

     @Operation(summary = "Get Population Record By Year", description = "Get a population record by year")
     @GetMapping("/year")
     public ResponseEntity<ApiResponse> getPopulationRecordByYear(@RequestParam("year") int year) {
         return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Population Record fetched successfully", populationRecordService.getPopulationRecordByYear(year)));
     }

     //read populations by excel
    @Operation(summary = "Read and save Population Records from Excel", description = "Read and save population records from an excel file")
    @PostMapping("/excel")
    public ResponseEntity<ApiResponse> readPopulationRecordsFromExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Population Records fetched successfully", populationRecordService.readPopulationRecordsFromExcel(file)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePopulationRecord(@PathVariable("id") UUID id, @RequestBody CreatePopulationRecordDto createPopulationRecordDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Population Record updated successfully", populationRecordService.updatePopulationRecord(id, createPopulationRecordDto)));
    }
}
