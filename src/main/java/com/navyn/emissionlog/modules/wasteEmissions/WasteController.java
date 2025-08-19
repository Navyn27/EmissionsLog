package com.navyn.emissionlog.modules.wasteEmissions;

import com.navyn.emissionlog.Enums.SolidWasteType;
import com.navyn.emissionlog.Enums.WasteType;
import com.navyn.emissionlog.utils.ApiResponse;
import com.navyn.emissionlog.Services.WasteService;

import com.navyn.emissionlog.modules.wasteEmissions.dtos.GeneralWasteByPopulationDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.IndustrialWasteDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.SolidWasteDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.WasteWaterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/waste")
@SecurityRequirement(name = "BearerAuth")
public class WasteController {

    @Autowired
    private WasteService wasteService;

    @Operation(summary = "Create Industrial Waste Water Data and calculate emissions")
    @PostMapping("/industrialWasteWater")
    public ResponseEntity<ApiResponse>createIndustrialWasteWaterData(@RequestBody IndustrialWasteDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Industrial waste water created successfully", wasteService.createIndustrialWasteWaterData(wasteData)));
    }

    @Operation(summary = "Create Solid Waste Data and calculate emissions")
    @PostMapping("/solidWaste")
    public ResponseEntity<ApiResponse> createSolidWasteData(@RequestBody SolidWasteDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Solid waste created successfully", wasteService.createSolidWasteData(wasteData)));
    }

    @Operation(summary = "Create Waste water Data and calculate emissions")
    @PostMapping("/wasteWater")
    public ResponseEntity<ApiResponse> createWasteWaterData(@RequestBody WasteWaterDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Waste water created successfully", wasteService.createWasteWaterData(wasteData)));
    }


    @Operation(summary = "Create Bio Treated Waste Water Data and calculate emissions")
    @PostMapping("/bioTreatedWasteWater")
    public ResponseEntity<ApiResponse> createBioTreatedWasteWaterData(@RequestBody GeneralWasteByPopulationDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Bio treated waste water created successfully", wasteService.createBioTreatedWasteWaterData(wasteData)));
    }

    @Operation(summary = "Create Bio Treated Waste Water Data and calculate emissions")
    @PostMapping("/burntWaste")
    public ResponseEntity<ApiResponse> createBurntWasteData(@RequestBody GeneralWasteByPopulationDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Burnt waste created successfully", wasteService.createBurntWasteData(wasteData)));
    }

    @Operation(summary = "Create Incineration Waste Data and calculate emissions")
    @PostMapping("/incinerationWaste")
    public ResponseEntity<ApiResponse> createWasteData(@RequestBody GeneralWasteByPopulationDto wasteData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "Incineration waste created successfully", wasteService.createIncinerationWasteData(wasteData)));
    }

    @Operation(summary = "Get all recorded Waste Data and their emissions")
    @GetMapping("/allWasteData")
    public ResponseEntity<ApiResponse> getAllWasteData() {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Waste data fetched successfully", wasteService.getAllWasteData()));
    }

    @Operation(summary = "Get Waste Data by type")
    @GetMapping("/wasteType/{wasteType}")
    public ResponseEntity<ApiResponse> getWasteDataByType(@PathVariable("wasteType") WasteType wasteType) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Waste data fetched successfully", wasteService.getWasteDataByType(wasteType)));
    }

    @Operation(summary = "Get Solid Waste Data and their emissions by solid waste Type")
    @GetMapping("/solidWaste/{wasteType}")
    public ResponseEntity<ApiResponse> getSolidWasteDataByType(@PathVariable("wasteType") SolidWasteType solidWasteType) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Waste data fetched successfully", wasteService.getSolidWasteDataByType(solidWasteType)));
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

}
