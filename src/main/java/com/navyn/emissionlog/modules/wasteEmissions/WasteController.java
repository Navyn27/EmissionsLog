package com.navyn.emissionlog.modules.wasteEmissions;

import com.navyn.emissionlog.Enums.Waste.SolidWasteType;
import com.navyn.emissionlog.Enums.Waste.WasteType;
import com.navyn.emissionlog.utils.ApiResponse;

import com.navyn.emissionlog.modules.wasteEmissions.dtos.GeneralWasteByPopulationDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.IndustrialWasteDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.SolidWasteDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.WasteWaterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/waste")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class WasteController {

    private final WasteService wasteService;

    @Operation(summary = "Create Industrial Waste Water Data and calculate emissions")
    @PostMapping("/industrialWasteWater")
    public ResponseEntity<ApiResponse>createIndustrialWasteWaterData(@Valid @RequestBody IndustrialWasteDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Industrial waste water created successfully", wasteService.createIndustrialWasteWaterData(wasteData)));
    }

    @Operation(summary = "Create Solid Waste Data and calculate emissions")
    @PostMapping("/solidWaste")
    public ResponseEntity<ApiResponse> createSolidWasteData(@Valid @RequestBody SolidWasteDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Solid waste created successfully", wasteService.createSolidWasteData(wasteData)));
    }

    @Operation(summary = "Create Waste water Data and calculate emissions")
    @PostMapping("/wasteWater")
    public ResponseEntity<ApiResponse> createWasteWaterData(@Valid @RequestBody WasteWaterDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Waste water created successfully", wasteService.createWasteWaterData(wasteData)));
    }


    @Operation(summary = "Create Bio Treated Waste Water Data and calculate emissions")
    @PostMapping("/bioTreatedWasteWater")
    public ResponseEntity<ApiResponse> createBioTreatedWasteWaterData(@Valid @RequestBody GeneralWasteByPopulationDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Bio treated waste water created successfully", wasteService.createBioTreatedWasteWaterData(wasteData)));
    }

    @Operation(summary = "Create Bio Treated Waste Water Data and calculate emissions")
    @PostMapping("/burntWaste")
    public ResponseEntity<ApiResponse> createBurntWasteData(@Valid @RequestBody GeneralWasteByPopulationDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Burnt waste created successfully", wasteService.createBurntWasteData(wasteData)));
    }

    @Operation(summary = "Create Incineration Waste Data and calculate emissions")
    @PostMapping("/incinerationWaste")
    public ResponseEntity<ApiResponse> createWasteData(@Valid @RequestBody GeneralWasteByPopulationDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Incineration waste created successfully", wasteService.createIncinerationWasteData(wasteData)));
    }

    @Operation(summary = "Get all recorded Waste Data and their emissions")
    @GetMapping("/allWasteData")
    public ResponseEntity<ApiResponse> getAllWasteData() {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Waste data fetched successfully", wasteService.getAllWasteData()));
    }

    @Operation(summary = "Get Waste Data by type")
    @GetMapping("/wasteType")
    public ResponseEntity<ApiResponse> getWasteData(@RequestParam(required = false, value = "wasteType") WasteType wasteType,
                                                          @RequestParam(required = false, value = "year") Integer year,
                                                          @RequestParam(required = false, value = "region") UUID regionId) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Waste data fetched successfully", wasteService.getWasteData(wasteType, year, regionId)));
    }

    @Operation(summary = "Get Solid Waste Data and their emissions by solid waste Type")
    @GetMapping("/solidWaste")
    public ResponseEntity<ApiResponse> getSolidWasteDataByType(@RequestParam(required = false, value = "wasteType") SolidWasteType solidWasteType,
                                                               @RequestParam(required = false, value = "year") Integer year,
                                                               @RequestParam(required = false, value = "region") UUID regionId) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Waste data fetched successfully", wasteService.getSolidWasteData(solidWasteType, year, regionId)));
    }
    
    // ============= UPDATE ENDPOINTS =============
    
    @Operation(summary = "Update Industrial Waste Water Data")
    @PutMapping("/industrialWasteWater/{id}")
    public ResponseEntity<ApiResponse> updateIndustrialWasteWaterData(
            @PathVariable UUID id,
            @Valid @RequestBody IndustrialWasteDto wasteData) {
        return ResponseEntity.ok(new ApiResponse(true, "Industrial waste water updated successfully", 
            wasteService.updateIndustrialWasteWaterData(id, wasteData)));
    }
    
    @Operation(summary = "Update Solid Waste Data")
    @PutMapping("/solidWaste/{id}")
    public ResponseEntity<ApiResponse> updateSolidWasteData(
            @PathVariable UUID id,
            @Valid @RequestBody SolidWasteDto wasteData) {
        return ResponseEntity.ok(new ApiResponse(true, "Solid waste updated successfully", 
            wasteService.updateSolidWasteData(id, wasteData)));
    }
    
    @Operation(summary = "Update Waste Water Data")
    @PutMapping("/wasteWater/{id}")
    public ResponseEntity<ApiResponse> updateWasteWaterData(
            @PathVariable UUID id,
            @Valid @RequestBody WasteWaterDto wasteData) {
        return ResponseEntity.ok(new ApiResponse(true, "Waste water updated successfully", 
            wasteService.updateWasteWaterData(id, wasteData)));
    }
    
    @Operation(summary = "Update Bio Treated Waste Water Data")
    @PutMapping("/bioTreatedWasteWater/{id}")
    public ResponseEntity<ApiResponse> updateBioTreatedWasteWaterData(
            @PathVariable UUID id,
            @Valid @RequestBody GeneralWasteByPopulationDto wasteData) {
        return ResponseEntity.ok(new ApiResponse(true, "Bio treated waste water updated successfully", 
            wasteService.updateBioTreatedWasteWaterData(id, wasteData)));
    }
    
    @Operation(summary = "Update Burnt Waste Data")
    @PutMapping("/burntWaste/{id}")
    public ResponseEntity<ApiResponse> updateBurntWasteData(
            @PathVariable UUID id,
            @Valid @RequestBody GeneralWasteByPopulationDto wasteData) {
        return ResponseEntity.ok(new ApiResponse(true, "Burnt waste updated successfully", 
            wasteService.updateBurntWasteData(id, wasteData)));
    }
    
    @Operation(summary = "Update Incineration Waste Data")
    @PutMapping("/incinerationWaste/{id}")
    public ResponseEntity<ApiResponse> updateIncinerationWasteData(
            @PathVariable UUID id,
            @Valid @RequestBody GeneralWasteByPopulationDto wasteData) {
        return ResponseEntity.ok(new ApiResponse(true, "Incineration waste updated successfully", 
            wasteService.updateIncinerationWasteData(id, wasteData)));
    }

    @Operation(summary = "Populate the DB with population records affiliated wasteData")
    @PostMapping("/populateWasteData/population")
    public ResponseEntity<ApiResponse> populateWasteDataWithPopulation() {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Waste data populated successfully", wasteService.populatePopulationAffiliatedWasteData()));
    }

    @Operation(summary = "Populate the DB with industrial wasteData")
    @PostMapping("/populateWasteData/industrial")
    public ResponseEntity<ApiResponse> populateWasteDataWithIndustrial(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Waste data populated successfully", wasteService.populateIndustrialWasteData(file)));
    }

    @Operation(summary = "Populate the DB with solid wasteData")
    @PostMapping("/populateWasteData/solid")
    public ResponseEntity<ApiResponse> populateWasteDataWithSolid(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Waste data populated successfully", wasteService.populateSolidWasteData(file)));
    }
    // ============= DELETE ENDPOINTS =============

    @Operation(summary = "Delete Industrial Waste Water Data", description = "Permanently deletes an industrial waste water record by ID. This action cannot be undone.")
    @DeleteMapping("/industrialWasteWater/{id}")
    public ResponseEntity<ApiResponse> deleteIndustrialWasteWaterData(@PathVariable UUID id) {
        wasteService.deleteIndustrialWasteWaterData(id);
        return ResponseEntity.ok(new ApiResponse(true, "Industrial waste water deleted successfully", null));
    }

    @Operation(summary = "Delete Solid Waste Data", description = "Permanently deletes a solid waste record by ID. This action cannot be undone.")
    @DeleteMapping("/solidWaste/{id}")
    public ResponseEntity<ApiResponse> deleteSolidWasteData(@PathVariable UUID id) {
        wasteService.deleteSolidWasteData(id);
        return ResponseEntity.ok(new ApiResponse(true, "Solid waste deleted successfully", null));
    }

    @Operation(summary = "Delete Waste Water Data", description = "Permanently deletes a waste water record by ID. This action cannot be undone.")
    @DeleteMapping("/wasteWater/{id}")
    public ResponseEntity<ApiResponse> deleteWasteWaterData(@PathVariable UUID id) {
        wasteService.deleteWasteWaterData(id);
        return ResponseEntity.ok(new ApiResponse(true, "Waste water deleted successfully", null));
    }

    @Operation(summary = "Delete Biologically Treated Waste Data", description = "Permanently deletes a biologically treated waste record by ID. This action cannot be undone.")
    @DeleteMapping("/bioTreatedWasteWater/{id}")
    public ResponseEntity<ApiResponse> deleteBioTreatedWasteWaterData(@PathVariable UUID id) {
        wasteService.deleteBioTreatedWasteWaterData(id);
        return ResponseEntity.ok(new ApiResponse(true, "Biologically treated waste deleted successfully", null));
    }

    @Operation(summary = "Delete Burnt Waste Data", description = "Permanently deletes a burnt waste record by ID. This action cannot be undone.")
    @DeleteMapping("/burntWaste/{id}")
    public ResponseEntity<ApiResponse> deleteBurntWasteData(@PathVariable UUID id) {
        wasteService.deleteBurntWasteData(id);
        return ResponseEntity.ok(new ApiResponse(true, "Burnt waste deleted successfully", null));
    }

    @Operation(summary = "Delete Incineration Waste Data", description = "Permanently deletes an incineration waste record by ID. This action cannot be undone.")
    @DeleteMapping("/incinerationWaste/{id}")
    public ResponseEntity<ApiResponse> deleteIncinerationWasteData(@PathVariable UUID id) {
        wasteService.deleteIncinerationWasteData(id);
        return ResponseEntity.ok(new ApiResponse(true, "Incineration waste deleted successfully", null));
    }


    // ============= MINI DASHBOARDS =============

    @Operation(summary = "Get Waste dashboard summary", description = "Retrieves waste emissions summary.")
    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse> getWasteDashboardSummary(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Waste dashboard summary fetched successfully",
                        wasteService.getWasteDashboardSummary(startingYear, endingYear)));
    }

    @Operation(summary = "Get Waste dashboard graph", description = "Retrieves waste emissions graph data by year.")
    @GetMapping("/dashboard/graph")
    public ResponseEntity<ApiResponse> getWasteDashboardGraph(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Waste dashboard graph fetched successfully",
                        wasteService.getWasteDashboardGraph(startingYear, endingYear)));
    }
}
