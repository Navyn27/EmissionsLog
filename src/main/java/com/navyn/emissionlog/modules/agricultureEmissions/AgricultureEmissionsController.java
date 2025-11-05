package com.navyn.emissionlog.modules.agricultureEmissions;

import com.navyn.emissionlog.Enums.Agriculture.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.DirectLandEmissions.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectLandEmissions.AtmosphericDepositionEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectLandEmissions.LeachingAndRunoffEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectManureEmissions.LeachingEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectManureEmissions.VolatilizationEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock.EntericFermentationEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock.ManureManagementEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.ManureManagementEmissions;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agriculture")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class AgricultureEmissionsController {

    private final AgricultureEmissionsService agricultureEmissionsService;

    //Get all liming emissions log
    @GetMapping("/limingEmissions")
    @Operation(summary = "Get all liming emissions")
    private ResponseEntity<ApiResponse> getAllLimingEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "limingMaterial") LimingMaterials limingMaterials) {
        return ResponseEntity.ok(new ApiResponse(true,"Liming emissions fetched successfully", agricultureEmissionsService.getAllLimingEmissions(year, limingMaterials)));
    }


    //Get all aquaculture emissions log
    @GetMapping("/aquacultureEmissions")
    @Operation(summary = "Get all aquaculture emissions")
    private ResponseEntity<ApiResponse> getAllAquacultureEmissions(@RequestParam(required = false, value = "year") Integer year) {
        return ResponseEntity.ok(new ApiResponse(true, "Aquaculture emissions fetched successfully", agricultureEmissionsService.getAllAquacultureEmissions(year)));
    }


    //Get all synthetic emissions log
    @GetMapping("/syntheticFertilizerEmissions")
    @Operation(summary = "Get all synthetic fertilizer emissions")
    private ResponseEntity<ApiResponse> getAllSyntheticFertilizerEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "fertilizerType") Fertilizers fertilizerType, @RequestParam(required = false, value = "cropType") CropTypes cropType) {
        return ResponseEntity.ok(new ApiResponse(true, "Synthetic fertilizer emissions fetched successfully", agricultureEmissionsService.getAllSyntheticFertilizerEmissions(year, cropType, fertilizerType)));
    }

    //Get all animal manure and compost emissions log
    @GetMapping("/ureaEmissions")
    @Operation(summary = "Get all urea emissions")
    private ResponseEntity<ApiResponse> getAllUreaEmissions(@RequestParam(required = false, value = "fertilizerName") String fertilizer, @RequestParam(required = false, value = "year") Integer year) {
        return ResponseEntity.ok(new ApiResponse(true, "Urea emissions fetched successfully", agricultureEmissionsService.getAllUreaEmissions(fertilizer, year)));
    }

    //Get all rice cultivation emissions log
    @GetMapping("/riceCultivationEmissions")
    @Operation(summary = "Get all rice cultivation emissions")
    private ResponseEntity<ApiResponse> getAllRiceCultivationEmissions(@RequestParam(required = false, value = "riceEcosystem") String riceEcosystem, @RequestParam(required = false, value = "waterRegime") WaterRegime waterRegime, @RequestParam(required = false, value = "year") Integer year) {
        return ResponseEntity.ok(new ApiResponse(true, "Rice cultivation emissions fetched successfully", agricultureEmissionsService.getAllRiceCultivationEmissions(riceEcosystem, waterRegime, year)));
    }

    //Get all manure and compost emissions log
    @GetMapping("/manureAndCompostEmissions")
    @Operation(summary = "Get all manure and compost emissions")
    private ResponseEntity<ApiResponse> getAllManureAndCompostEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "amendmentType") OrganicAmendmentTypes amendmentType, @RequestParam(required = false, value = "species") LivestockSpecies species) {
        return ResponseEntity.ok(new ApiResponse(true, "Manure and Compost emissions fetched successfully", agricultureEmissionsService.getAllAnimalManureAndCompostEmissions(year, amendmentType, species)));
    }

    //Get all enteric fermentation emissions log
    @GetMapping("/entericFermentationEmissions")
    @Operation(summary = "Get all enteric fermentation emissions")
    private ResponseEntity<ApiResponse> getAllEntericFermentationEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "species") LivestockSpecies species) {
        return ResponseEntity.ok(new ApiResponse(true, "Enteric fermentation emissions fetched successfully", agricultureEmissionsService.getAllEntericFermentationEmissions(year, species)));
    }

    //Create all liming emissions log
    @PostMapping("/limingEmissions")
    @Operation(summary = "Create new liming emissions record")
    private ResponseEntity<ApiResponse> createLimingEmissions(@Valid @RequestBody LimingEmissionsDto limingEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Liming emissions created successfully", agricultureEmissionsService.createLimingEmissions(limingEmissionsDto)));
    }

    //Create all urea emissions log
    @PostMapping("/ureaEmissions")
    @Operation(summary = "Create new urea emissions record")
    private ResponseEntity<ApiResponse> createUreaEmissions(@Valid @RequestBody UreaEmissionsDto ureaEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Urea emissions created successfully", agricultureEmissionsService.createUreaEmissions(ureaEmissionsDto)));
    }

    //Create all aquaculture emissions log
    @PostMapping("/aquacultureEmissions")
    @Operation(summary = "Create new aquaculture emissions record")
    private ResponseEntity<ApiResponse> createAquacultureEmissions(@Valid @RequestBody AquacultureEmissionsDto aquacultureEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Aquaculture emissions created successfully", agricultureEmissionsService.createAquacultureEmissions(aquacultureEmissionsDto)));
    }

    //Create all synthetic emissions log
    @PostMapping("/syntheticFertilizerEmissions")
    @Operation(summary = "Create new synthetic fertilizer emissions record")
    private ResponseEntity<ApiResponse> createSyntheticFertilizerEmissions(@Valid @RequestBody SyntheticFertilizerEmissionsDto syntheticFertilizerEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Synthetic fertilizer emissions created successfully", agricultureEmissionsService.createSyntheticFertilizerEmissions(syntheticFertilizerEmissionsDto)));
    }

    //Create all rice cultivation emissions log
    @PostMapping("/riceCultivationEmissions")
    @Operation(summary = "Create new rice cultivation emissions record")
    private ResponseEntity<ApiResponse> createRiceCultivationEmissions(@Valid @RequestBody RiceCultivationEmissionsDto riceCultivationEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Rice cultivation emissions created successfully", agricultureEmissionsService.createRiceCultivationEmissions(riceCultivationEmissionsDto)));
    }

    //Create manure and compost emissions log
    @PostMapping("/manureAndCompostEmissions")
    @Operation(summary = "Create new manure and compost emissions record")
    private ResponseEntity<ApiResponse> createManureAndCompostEmissions(@Valid @RequestBody AnimalManureAndCompostEmissionsDto manureAndCompostEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Manure and Compost emissions created successfully", agricultureEmissionsService.createAnimalManureAndCompostEmissions(manureAndCompostEmissionsDto)));
    }

    //Create enteric fermentation emissions log
    @PostMapping("/entericFermentationEmissions")
    @Operation(summary = "Create new enteric fermentation emissions record")
    private ResponseEntity<ApiResponse> createEntericFermentationEmissions(@Valid @RequestBody EntericFermentationEmissionsDto entericFermentationEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Enteric fermentation emissions created successfully", agricultureEmissionsService.createEntericFermentationEmissions(entericFermentationEmissionsDto)));
    }

    //Create burning emissions log
    @PostMapping("/burningEmissions")
    @Operation(summary = "Create new burning emissions record")
    private ResponseEntity<ApiResponse> createBurningEmissions(@Valid @RequestBody BurningEmissionsDto burningEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Burning emissions created successfully", agricultureEmissionsService.createBurningEmissions(burningEmissionsDto)));
    }

    //Get all burning emissions logs
    @GetMapping("/burningEmissions")
    @Operation(summary = "Get all burning emissions")
    private ResponseEntity<ApiResponse> getAllBurningEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "forestType") BurningAgentType forestType) {
        return ResponseEntity.ok(new ApiResponse(true, "Burning emissions fetched successfully", agricultureEmissionsService.getAllBurningEmissions(year, forestType)));
    }

    //Create crop residue emissions logs
    @PostMapping("/cropResidueEmissions")
    @Operation(summary = "Create new crop residue emissions record")
    private ResponseEntity<ApiResponse> createCropResidueEmissions(@Valid @RequestBody CropResiduesEmissionsDto cropResidueEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Crop residue emissions created successfully", agricultureEmissionsService.createCropResidueEmissions(cropResidueEmissionsDto)));
    }

    //Get all crop residue emissions logs
    @GetMapping("/cropResidueEmissions")
    @Operation(summary = "Get all crop residue emissions")
    private ResponseEntity<ApiResponse> getAllCropResidueEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "cropType") CropResiduesCropType cropType, @RequestParam(required = false, value = "landUseCategory") LandUseCategory landUseCategory) {
        return ResponseEntity.ok(new ApiResponse(true, "Crop residue emissions fetched successfully", agricultureEmissionsService.getAllCropResidueEmissions(year, cropType, landUseCategory)));
    }

    //Create pasture excretion emissions logs
    @PostMapping("/pastureExcretionEmissions")
    @Operation(summary = "Create new pasture excretion emissions record")
    private ResponseEntity<ApiResponse> createPastureExcretionEmissions(@Valid @RequestBody PastureExcretionsEmissionsDto pastureExcretionEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Pasture excretion emissions created successfully", agricultureEmissionsService.createPastureExcretionEmissions(pastureExcretionEmissionsDto)));
    }

    //Get all pasture excretion emissions logs
    @GetMapping("/pastureExcretionEmissions")
    @Operation(summary = "Get all pasture excretion emissions")
    private ResponseEntity<ApiResponse> getAllPastureExcretionEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "category") LivestockSpecies species, @RequestParam(required = false, value = "mms") MMS mms) {
        return ResponseEntity.ok(new ApiResponse(true, "Pasture excretion emissions fetched successfully", agricultureEmissionsService.getAllPastureExcretionEmissions(year, species, mms)));
    }

    //Create Mineral Soil Emissions logs
    @PostMapping("/mineralSoilEmissions")
    @Operation(summary = "Create new mineral soil emissions record")
    private ResponseEntity<ApiResponse> createMineralSoilEmissions(@Valid @RequestBody MineralSoilEmissionsDto mineralSoilEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Mineral soil emissions created successfully", agricultureEmissionsService.createMineralSoilEmissions(mineralSoilEmissionsDto)));
    }

    //Get all Mineral Soil Emissions logs
    @GetMapping("/mineralSoilEmissions")
    @Operation(summary = "Get all mineral soil emissions")
    private ResponseEntity<ApiResponse> getAllMineralSoilEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "initialLandUse") LandUseCategory initialLandUse, @RequestParam(required = false, value = "landUseInReportingYear") LandUseCategory landUseInReportingYear) {
        return ResponseEntity.ok(new ApiResponse(true, "Mineral soil emissions fetched successfully", agricultureEmissionsService.getAllMineralSoilEmissions(year, initialLandUse, landUseInReportingYear)));
    }

    //Create Volatilization Emissions logs
    @PostMapping("/volatilizationEmissions")
    @Operation(summary = "Create new volatilization emissions record")
    private ResponseEntity<ApiResponse> createVolatilizationEmissions(@Valid @RequestBody VolatilizationEmissionsDto volatilizationEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Volatilization emissions created successfully", agricultureEmissionsService.createVolatilizationEmissions(volatilizationEmissionsDto)));
    }

    //Get all Volatilization Emissions logs
    @GetMapping("/volatilizationEmissions")
    @Operation(summary = "Get all volatilization emissions")
    private ResponseEntity<ApiResponse> getAllVolatilizationEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "mms") MMS mms, @RequestParam(required = false, value = "species") LivestockSpecies species) {
        return ResponseEntity.ok(new ApiResponse(true, "Volatilization emissions fetched successfully", agricultureEmissionsService.getAllVolatilizationEmissions(year, mms, species)));
    }

    //Create Leaching Emissions logs
    @PostMapping("/leachingEmissions")
    @Operation(summary = "Create new leaching emissions record")
    private ResponseEntity<ApiResponse> createLeachingEmissions(@Valid @RequestBody LeachingEmissionsDto leachingEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Leaching emissions created successfully", agricultureEmissionsService.createLeachingEmissions(leachingEmissionsDto)));
    }

    //Get all Leaching Emissions logs
    @GetMapping("/leachingEmissions")
    @Operation(summary = "Get all leaching emissions")
    private ResponseEntity<ApiResponse> getAllLeachingEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "mms") MMS mms, @RequestParam(required = false, value = "species") LivestockSpecies species) {
        return ResponseEntity.ok(new ApiResponse(true, "Leaching emissions fetched successfully", agricultureEmissionsService.getAllLeachingEmissions(year, mms, species)));
    }

    //Atmospheric N Deposition Emissions logs
    @PostMapping("/atmosphericDepositionEmissions")
    @Operation(summary = "Create new atmospheric N deposition emissions record")
    private ResponseEntity<ApiResponse> createAtmosphericNDepositionEmissions(@Valid @RequestBody AtmosphericDepositionEmissionsDto atmosphericNDepositionEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Atmospheric N deposition emissions created successfully", agricultureEmissionsService.createAtmosphericNDepositionEmissions(atmosphericNDepositionEmissionsDto)));
    }

    //Get all Atmospheric N Deposition Emissions logs
    @GetMapping("/atmosphericNDepositionEmissions")
    @Operation(summary = "Get all atmospheric N deposition emissions")
    private ResponseEntity<ApiResponse> getAllAtmosphericNDepositionEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "landUseCategory") LandUseCategory landUseCategory) {
        return ResponseEntity.ok(new ApiResponse(true, "Atmospheric N deposition emissions fetched successfully", agricultureEmissionsService.getAllAtmosphericNDepositionEmissions(year, landUseCategory)));
    }

    //Create LeachingAndRunoff Emissions logs
    @PostMapping("/leachingAndRunoffEmissions")
    @Operation(summary = "Create new leaching and runoff emissions record")
    private ResponseEntity<ApiResponse> createLeachingAndRunoffEmissions(@Valid @RequestBody LeachingAndRunoffEmissionsDto leachingAndRunoffEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Leaching and runoff emissions created successfully", agricultureEmissionsService.createLeachingAndRunoffEmissions(leachingAndRunoffEmissionsDto)));
    }

    //Get all LeachingAndRunoff Emissions logs
    @GetMapping("/leachingAndRunoffEmissions")
    @Operation(summary = "Get all leaching and runoff emissions")
    private ResponseEntity<ApiResponse> getAllLeachingAndRunoffEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "landUseCategory") LandUseCategory landUseCategory) {
        return ResponseEntity.ok(new ApiResponse(true, "Leaching and runoff emissions fetched successfully", agricultureEmissionsService.getAllLeachingAndRunoffEmissions(year, landUseCategory)));
    }

    //Create manure management emissions log
    @PostMapping("/manureManagementEmissions")
    @Operation(summary = "Create new manure management emissions record")
    private ResponseEntity<ApiResponse> createManureManagementEmissions(@Valid @RequestBody ManureManagementEmissionsDto dto) {
        ManureManagementEmissions emissions = agricultureEmissionsService.createManureManagementEmissions(dto);
        return ResponseEntity.ok(new ApiResponse(true, "Manure management emissions created successfully", emissions));
    }

    //Get all manure management emissions logs
    @GetMapping("/manureManagementEmissions")
    @Operation(summary = "Get all manure management emissions")
    private ResponseEntity<ApiResponse> getAllManureManagementEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "species") ManureManagementLivestock species,
            @RequestParam(required = false, value = "mms") ManureManagementSystem mms) {
        List<ManureManagementEmissions> emissions = agricultureEmissionsService.getAllManureManagementEmissions(year, species, mms);
        return ResponseEntity.ok(new ApiResponse(true, "Manure management emissions fetched successfully", emissions));
    }
    
    // ============= MINI DASHBOARDS =============
    
    @Operation(summary = "Get Agriculture dashboard summary", description = "Retrieves agriculture emissions summary from all 7 modules.")
    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse> getAgricultureDashboardSummary(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Agriculture dashboard summary fetched successfully", 
                        agricultureEmissionsService.getAgricultureDashboardSummary(startingYear, endingYear)));
    }
    
    @Operation(summary = "Get Agriculture dashboard graph", description = "Retrieves agriculture emissions graph data by year.")
    @GetMapping("/dashboard/graph")
    public ResponseEntity<ApiResponse> getAgricultureDashboardGraph(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Agriculture dashboard graph fetched successfully", 
                        agricultureEmissionsService.getAgricultureDashboardGraph(startingYear, endingYear)));
    }
}
