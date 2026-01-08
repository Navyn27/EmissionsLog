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
@RequestMapping("/agriculture")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class AgricultureEmissionsController {

    private final AgricultureEmissionsService agricultureEmissionsService;

    // Get all liming emissions log
    @GetMapping("/limingEmissions")
    @Operation(summary = "Get all liming emissions")
    public ResponseEntity<ApiResponse> getAllLimingEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "limingMaterial") LimingMaterials limingMaterials) {
        return ResponseEntity.ok(new ApiResponse(true, "Liming emissions fetched successfully",
                agricultureEmissionsService.getAllLimingEmissions(year, limingMaterials)));
    }

    // Get all aquaculture emissions log
    @GetMapping("/aquacultureEmissions")
    @Operation(summary = "Get all aquaculture emissions")
    public ResponseEntity<ApiResponse> getAllAquacultureEmissions(
            @RequestParam(required = false, value = "year") Integer year) {
        return ResponseEntity.ok(new ApiResponse(true, "Aquaculture emissions fetched successfully",
                agricultureEmissionsService.getAllAquacultureEmissions(year)));
    }

    // Get all synthetic emissions log
    @GetMapping("/syntheticFertilizerEmissions")
    @Operation(summary = "Get all synthetic fertilizer emissions")
    public ResponseEntity<ApiResponse> getAllSyntheticFertilizerEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "fertilizerType") Fertilizers fertilizerType,
            @RequestParam(required = false, value = "cropType") CropTypes cropType) {
        return ResponseEntity.ok(new ApiResponse(true, "Synthetic fertilizer emissions fetched successfully",
                agricultureEmissionsService.getAllSyntheticFertilizerEmissions(year, cropType, fertilizerType)));
    }

    // Get all animal manure and compost emissions log
    @GetMapping("/ureaEmissions")
    @Operation(summary = "Get all urea emissions")
    public ResponseEntity<ApiResponse> getAllUreaEmissions(
            @RequestParam(required = false, value = "fertilizerName") String fertilizer,
            @RequestParam(required = false, value = "year") Integer year) {
        return ResponseEntity.ok(new ApiResponse(true, "Urea emissions fetched successfully",
                agricultureEmissionsService.getAllUreaEmissions(fertilizer, year)));
    }

    // Get all rice cultivation emissions log
    @GetMapping("/riceCultivationEmissions")
    @Operation(summary = "Get all rice cultivation emissions")
    public ResponseEntity<ApiResponse> getAllRiceCultivationEmissions(
            @RequestParam(required = false, value = "riceEcosystem") String riceEcosystem,
            @RequestParam(required = false, value = "waterRegime") WaterRegime waterRegime,
            @RequestParam(required = false, value = "year") Integer year) {
        return ResponseEntity.ok(new ApiResponse(true, "Rice cultivation emissions fetched successfully",
                agricultureEmissionsService.getAllRiceCultivationEmissions(riceEcosystem, waterRegime, year)));
    }

    // Get all manure and compost emissions log
    @GetMapping("/manureAndCompostEmissions")
    @Operation(summary = "Get all manure and compost emissions")
    public ResponseEntity<ApiResponse> getAllManureAndCompostEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "amendmentType") OrganicAmendmentTypes amendmentType,
            @RequestParam(required = false, value = "species") LivestockSpecies species) {
        return ResponseEntity.ok(new ApiResponse(true, "Manure and Compost emissions fetched successfully",
                agricultureEmissionsService.getAllAnimalManureAndCompostEmissions(year, amendmentType, species)));
    }

    // Get all enteric fermentation emissions log
    @GetMapping("/entericFermentationEmissions")
    @Operation(summary = "Get all enteric fermentation emissions")
    public ResponseEntity<ApiResponse> getAllEntericFermentationEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "species") LivestockSpecies species) {
        return ResponseEntity.ok(new ApiResponse(true, "Enteric fermentation emissions fetched successfully",
                agricultureEmissionsService.getAllEntericFermentationEmissions(year, species)));
    }

    // Create all liming emissions log
    @PostMapping("/limingEmissions")
    @Operation(summary = "Create new liming emissions record")
    public ResponseEntity<ApiResponse> createLimingEmissions(
            @Valid @RequestBody LimingEmissionsDto limingEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Liming emissions created successfully",
                agricultureEmissionsService.createLimingEmissions(limingEmissionsDto)));
    }

    // Create all urea emissions log
    @PostMapping("/ureaEmissions")
    @Operation(summary = "Create new urea emissions record")
    public ResponseEntity<ApiResponse> createUreaEmissions(@Valid @RequestBody UreaEmissionsDto ureaEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Urea emissions created successfully",
                agricultureEmissionsService.createUreaEmissions(ureaEmissionsDto)));
    }

    // Create all aquaculture emissions log
    @PostMapping("/aquacultureEmissions")
    @Operation(summary = "Create new aquaculture emissions record")
    public ResponseEntity<ApiResponse> createAquacultureEmissions(
            @Valid @RequestBody AquacultureEmissionsDto aquacultureEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Aquaculture emissions created successfully",
                agricultureEmissionsService.createAquacultureEmissions(aquacultureEmissionsDto)));
    }

    // Create all synthetic emissions log
    @PostMapping("/syntheticFertilizerEmissions")
    @Operation(summary = "Create new synthetic fertilizer emissions record")
    public ResponseEntity<ApiResponse> createSyntheticFertilizerEmissions(
            @Valid @RequestBody SyntheticFertilizerEmissionsDto syntheticFertilizerEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Synthetic fertilizer emissions created successfully",
                agricultureEmissionsService.createSyntheticFertilizerEmissions(syntheticFertilizerEmissionsDto)));
    }

    // Create all rice cultivation emissions log
    @PostMapping("/riceCultivationEmissions")
    @Operation(summary = "Create new rice cultivation emissions record")
    public ResponseEntity<ApiResponse> createRiceCultivationEmissions(
            @Valid @RequestBody RiceCultivationEmissionsDto riceCultivationEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Rice cultivation emissions created successfully",
                agricultureEmissionsService.createRiceCultivationEmissions(riceCultivationEmissionsDto)));
    }

    // Create manure and compost emissions log
    @PostMapping("/manureAndCompostEmissions")
    @Operation(summary = "Create new manure and compost emissions record")
    public ResponseEntity<ApiResponse> createManureAndCompostEmissions(
            @Valid @RequestBody AnimalManureAndCompostEmissionsDto manureAndCompostEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Manure and Compost emissions created successfully",
                agricultureEmissionsService.createAnimalManureAndCompostEmissions(manureAndCompostEmissionsDto)));
    }

    // Create enteric fermentation emissions log
    @PostMapping("/entericFermentationEmissions")
    @Operation(summary = "Create new enteric fermentation emissions record")
    public ResponseEntity<ApiResponse> createEntericFermentationEmissions(
            @Valid @RequestBody EntericFermentationEmissionsDto entericFermentationEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Enteric fermentation emissions created successfully",
                agricultureEmissionsService.createEntericFermentationEmissions(entericFermentationEmissionsDto)));
    }

    @GetMapping("/entericFermentationEmissions/template")
    @Operation(summary = "Download Enteric Fermentation Emissions Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Enteric Fermentation Emissions records")
    public ResponseEntity<byte[]> downloadEntericFermentationExcelTemplate() {
        byte[] templateBytes = agricultureEmissionsService.generateEntericFermentationExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Enteric_Fermentation_Emissions_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/entericFermentationEmissions/excel")
    @Operation(summary = "Upload Enteric Fermentation Emissions records from Excel file", description = "Uploads multiple Enteric Fermentation Emissions records from an Excel file. Records with duplicate year+Species combinations will be skipped.")
    public ResponseEntity<ApiResponse> createEntericFermentationEmissionsFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = agricultureEmissionsService.createEntericFermentationEmissionsFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped.",
                savedCount,
                skippedCount);

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }

    // Create burning emissions log
    @PostMapping("/burningEmissions")
    @Operation(summary = "Create new burning emissions record")
    public ResponseEntity<ApiResponse> createBurningEmissions(
            @Valid @RequestBody BurningEmissionsDto burningEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Burning emissions created successfully",
                agricultureEmissionsService.createBurningEmissions(burningEmissionsDto)));
    }

    // Get all burning emissions logs
    @GetMapping("/burningEmissions")
    @Operation(summary = "Get all burning emissions")
    public ResponseEntity<ApiResponse> getAllBurningEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "forestType") BurningAgentType forestType) {
        return ResponseEntity.ok(new ApiResponse(true, "Burning emissions fetched successfully",
                agricultureEmissionsService.getAllBurningEmissions(year, forestType)));
    }

    // Create crop residue emissions logs
    @PostMapping("/cropResidueEmissions")
    @Operation(summary = "Create new crop residue emissions record")
    public ResponseEntity<ApiResponse> createCropResidueEmissions(
            @Valid @RequestBody CropResiduesEmissionsDto cropResidueEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Crop residue emissions created successfully",
                agricultureEmissionsService.createCropResidueEmissions(cropResidueEmissionsDto)));
    }

    // Get all crop residue emissions logs
    @GetMapping("/cropResidueEmissions")
    @Operation(summary = "Get all crop residue emissions")
    public ResponseEntity<ApiResponse> getAllCropResidueEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "cropType") CropResiduesCropType cropType,
            @RequestParam(required = false, value = "landUseCategory") LandUseCategory landUseCategory) {
        return ResponseEntity.ok(new ApiResponse(true, "Crop residue emissions fetched successfully",
                agricultureEmissionsService.getAllCropResidueEmissions(year, cropType, landUseCategory)));
    }

    // Create pasture excretion emissions logs
    @PostMapping("/pastureExcretionEmissions")
    @Operation(summary = "Create new pasture excretion emissions record")
    public ResponseEntity<ApiResponse> createPastureExcretionEmissions(
            @Valid @RequestBody PastureExcretionsEmissionsDto pastureExcretionEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Pasture excretion emissions created successfully",
                agricultureEmissionsService.createPastureExcretionEmissions(pastureExcretionEmissionsDto)));
    }

    // Get all pasture excretion emissions logs
    @GetMapping("/pastureExcretionEmissions")
    @Operation(summary = "Get all pasture excretion emissions")
    public ResponseEntity<ApiResponse> getAllPastureExcretionEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "category") LivestockSpecies species,
            @RequestParam(required = false, value = "mms") MMS mms) {
        return ResponseEntity.ok(new ApiResponse(true, "Pasture excretion emissions fetched successfully",
                agricultureEmissionsService.getAllPastureExcretionEmissions(year, species, mms)));
    }

    // Create Mineral Soil Emissions logs
    @PostMapping("/mineralSoilEmissions")
    @Operation(summary = "Create new mineral soil emissions record")
    public ResponseEntity<ApiResponse> createMineralSoilEmissions(
            @Valid @RequestBody MineralSoilEmissionsDto mineralSoilEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Mineral soil emissions created successfully",
                agricultureEmissionsService.createMineralSoilEmissions(mineralSoilEmissionsDto)));
    }

    // Get all Mineral Soil Emissions logs
    @GetMapping("/mineralSoilEmissions")
    @Operation(summary = "Get all mineral soil emissions")
    public ResponseEntity<ApiResponse> getAllMineralSoilEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "initialLandUse") LandUseCategory initialLandUse,
            @RequestParam(required = false, value = "landUseInReportingYear") LandUseCategory landUseInReportingYear) {
        return ResponseEntity.ok(new ApiResponse(true, "Mineral soil emissions fetched successfully",
                agricultureEmissionsService.getAllMineralSoilEmissions(year, initialLandUse, landUseInReportingYear)));
    }

    // Create Volatilization Emissions logs
    @PostMapping("/volatilizationEmissions")
    @Operation(summary = "Create new volatilization emissions record")
    public ResponseEntity<ApiResponse> createVolatilizationEmissions(
            @Valid @RequestBody VolatilizationEmissionsDto volatilizationEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Volatilization emissions created successfully",
                agricultureEmissionsService.createVolatilizationEmissions(volatilizationEmissionsDto)));
    }

    // Get all Volatilization Emissions logs
    @GetMapping("/volatilizationEmissions")
    @Operation(summary = "Get all volatilization emissions")
    public ResponseEntity<ApiResponse> getAllVolatilizationEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "mms") MMS mms,
            @RequestParam(required = false, value = "species") LivestockSpecies species) {
        return ResponseEntity.ok(new ApiResponse(true, "Volatilization emissions fetched successfully",
                agricultureEmissionsService.getAllVolatilizationEmissions(year, mms, species)));
    }

    // Create Leaching Emissions logs
    @PostMapping("/leachingEmissions")
    @Operation(summary = "Create new leaching emissions record")
    public ResponseEntity<ApiResponse> createLeachingEmissions(
            @Valid @RequestBody LeachingEmissionsDto leachingEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Leaching emissions created successfully",
                agricultureEmissionsService.createLeachingEmissions(leachingEmissionsDto)));
    }

    // Get all Leaching Emissions logs
    @GetMapping("/leachingEmissions")
    @Operation(summary = "Get all leaching emissions")
    public ResponseEntity<ApiResponse> getAllLeachingEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "mms") MMS mms,
            @RequestParam(required = false, value = "species") LivestockSpecies species) {
        return ResponseEntity.ok(new ApiResponse(true, "Leaching emissions fetched successfully",
                agricultureEmissionsService.getAllLeachingEmissions(year, mms, species)));
    }

    // Atmospheric N Deposition Emissions logs
    @PostMapping("/atmosphericDepositionEmissions")
    @Operation(summary = "Create new atmospheric N deposition emissions record")
    public ResponseEntity<ApiResponse> createAtmosphericNDepositionEmissions(
            @Valid @RequestBody AtmosphericDepositionEmissionsDto atmosphericNDepositionEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Atmospheric N deposition emissions created successfully",
                agricultureEmissionsService.createAtmosphericNDepositionEmissions(atmosphericNDepositionEmissionsDto)));
    }

    // Get all Atmospheric N Deposition Emissions logs
    @GetMapping("/atmosphericNDepositionEmissions")
    @Operation(summary = "Get all atmospheric N deposition emissions")
    public ResponseEntity<ApiResponse> getAllAtmosphericNDepositionEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "landUseCategory") LandUseCategory landUseCategory) {
        return ResponseEntity.ok(new ApiResponse(true, "Atmospheric N deposition emissions fetched successfully",
                agricultureEmissionsService.getAllAtmosphericNDepositionEmissions(year, landUseCategory)));
    }

    // Create LeachingAndRunoff Emissions logs
    @PostMapping("/leachingAndRunoffEmissions")
    @Operation(summary = "Create new leaching and runoff emissions record")
    public ResponseEntity<ApiResponse> createLeachingAndRunoffEmissions(
            @Valid @RequestBody LeachingAndRunoffEmissionsDto leachingAndRunoffEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Leaching and runoff emissions created successfully",
                agricultureEmissionsService.createLeachingAndRunoffEmissions(leachingAndRunoffEmissionsDto)));
    }

    // Get all LeachingAndRunoff Emissions logs
    @GetMapping("/leachingAndRunoffEmissions")
    @Operation(summary = "Get all leaching and runoff emissions")
    public ResponseEntity<ApiResponse> getAllLeachingAndRunoffEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "landUseCategory") LandUseCategory landUseCategory) {
        return ResponseEntity.ok(new ApiResponse(true, "Leaching and runoff emissions fetched successfully",
                agricultureEmissionsService.getAllLeachingAndRunoffEmissions(year, landUseCategory)));
    }

    // Create manure management emissions log
    @PostMapping("/manureManagementEmissions")
    @Operation(summary = "Create new manure management emissions record")
    public ResponseEntity<ApiResponse> createManureManagementEmissions(
            @Valid @RequestBody ManureManagementEmissionsDto dto) {
        ManureManagementEmissions emissions = agricultureEmissionsService.createManureManagementEmissions(dto);
        return ResponseEntity.ok(new ApiResponse(true, "Manure management emissions created successfully", emissions));
    }

    // Get all manure management emissions logs
    @GetMapping("/manureManagementEmissions")
    @Operation(summary = "Get all manure management emissions")
    public ResponseEntity<ApiResponse> getAllManureManagementEmissions(
            @RequestParam(required = false, value = "year") Integer year,
            @RequestParam(required = false, value = "species") ManureManagementLivestock species) {
        List<ManureManagementEmissions> emissions = agricultureEmissionsService.getAllManureManagementEmissions(year,
                species);
        return ResponseEntity.ok(new ApiResponse(true, "Manure management emissions fetched successfully", emissions));
    }

    // Get manure management emissions by ID
    @GetMapping("/manureManagementEmissions/{id}")
    @Operation(summary = "Get manure management emissions by ID")
    public ResponseEntity<ApiResponse> getManureManagementEmissionsById(@PathVariable UUID id) {
        ManureManagementEmissions emissions = agricultureEmissionsService.getManureManagementEmissionsById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Manure management emissions fetched successfully", emissions));
    }

    @PutMapping("/aquacultureEmissions/{id}")
    @Operation(summary = "Update aquaculture emissions record")
    public ResponseEntity<ApiResponse> updateAquacultureEmissions(@PathVariable UUID id,
            @Valid @RequestBody AquacultureEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Aquaculture emissions updated successfully",
                agricultureEmissionsService.updateAquacultureEmissions(id, dto)));
    }

    @PutMapping("/entericFermentationEmissions/{id}")
    @Operation(summary = "Update enteric fermentation emissions record")
    public ResponseEntity<ApiResponse> updateEntericFermentationEmissions(@PathVariable UUID id,
            @Valid @RequestBody EntericFermentationEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Enteric Fermentation emissions updated successfully",
                agricultureEmissionsService.updateEntericFermentationEmissions(id, dto)));
    }

    @PutMapping("/limingEmissions/{id}")
    @Operation(summary = "Update liming emissions record")
    public ResponseEntity<ApiResponse> updateLimingEmissions(@PathVariable UUID id,
            @Valid @RequestBody LimingEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Liming emissions updated successfully",
                agricultureEmissionsService.updateLimingEmissions(id, dto)));
    }

    @PutMapping("/animalManureAndCompostEmissions/{id}")
    @Operation(summary = "Update animal manure and compost emissions record")
    public ResponseEntity<ApiResponse> updateAnimalManureAndCompostEmissions(@PathVariable UUID id,
            @Valid @RequestBody AnimalManureAndCompostEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Animal Manure and Compost emissions updated successfully",
                agricultureEmissionsService.updateAnimalManureAndCompostEmissions(id, dto)));
    }

    @PutMapping("/riceCultivationEmissions/{id}")
    @Operation(summary = "Update rice cultivation emissions record")
    public ResponseEntity<ApiResponse> updateRiceCultivationEmissions(@PathVariable UUID id,
            @Valid @RequestBody RiceCultivationEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Rice Cultivation emissions updated successfully",
                agricultureEmissionsService.updateRiceCultivationEmissions(id, dto)));
    }

    @PutMapping("/syntheticFertilizerEmissions/{id}")
    @Operation(summary = "Update synthetic fertilizer emissions record")
    public ResponseEntity<ApiResponse> updateSyntheticFertilizerEmissions(@PathVariable UUID id,
            @Valid @RequestBody SyntheticFertilizerEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Synthetic Fertilizer emissions updated successfully",
                agricultureEmissionsService.updateSyntheticFertilizerEmissions(id, dto)));
    }

    @PutMapping("/ureaEmissions/{id}")
    @Operation(summary = "Update urea emissions record")
    public ResponseEntity<ApiResponse> updateUreaEmissions(@PathVariable UUID id,
            @Valid @RequestBody UreaEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Urea emissions updated successfully",
                agricultureEmissionsService.updateUreaEmissions(id, dto)));
    }

    @PutMapping("/burningEmissions/{id}")
    @Operation(summary = "Update burning emissions record")
    public ResponseEntity<ApiResponse> updateBurningEmissions(@PathVariable UUID id,
            @Valid @RequestBody BurningEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Burning emissions updated successfully",
                agricultureEmissionsService.updateBurningEmissions(id, dto)));
    }

    @PutMapping("/cropResidueEmissions/{id}")
    @Operation(summary = "Update crop residue emissions record")
    public ResponseEntity<ApiResponse> updateCropResidueEmissions(@PathVariable UUID id,
            @Valid @RequestBody CropResiduesEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Crop Residue emissions updated successfully",
                agricultureEmissionsService.updateCropResidueEmissions(id, dto)));
    }

    @PutMapping("/pastureExcretionEmissions/{id}")
    @Operation(summary = "Update pasture excretion emissions record")
    public ResponseEntity<ApiResponse> updatePastureExcretionEmissions(@PathVariable UUID id,
            @Valid @RequestBody PastureExcretionsEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Pasture Excretion emissions updated successfully",
                agricultureEmissionsService.updatePastureExcretionEmissions(id, dto)));
    }

    @PutMapping("/mineralSoilEmissions/{id}")
    @Operation(summary = "Update mineral soil emissions record")
    public ResponseEntity<ApiResponse> updateMineralSoilEmissions(@PathVariable UUID id,
            @Valid @RequestBody MineralSoilEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Mineral Soil emissions updated successfully",
                agricultureEmissionsService.updateMineralSoilEmissions(id, dto)));
    }

    @PutMapping("/volatilizationEmissions/{id}")
    @Operation(summary = "Update volatilization emissions record")
    public ResponseEntity<ApiResponse> updateVolatilizationEmissions(@PathVariable UUID id,
            @Valid @RequestBody VolatilizationEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Volatilization emissions updated successfully",
                agricultureEmissionsService.updateVolatilizationEmissions(id, dto)));
    }

    @PutMapping("/leachingEmissions/{id}")
    @Operation(summary = "Update leaching emissions record")
    public ResponseEntity<ApiResponse> updateLeachingEmissions(@PathVariable UUID id,
            @Valid @RequestBody LeachingEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Leaching emissions updated successfully",
                agricultureEmissionsService.updateLeachingEmissions(id, dto)));
    }

    @PutMapping("/atmosphericNDepositionEmissions/{id}")
    @Operation(summary = "Update atmospheric N deposition emissions record")
    public ResponseEntity<ApiResponse> updateAtmosphericNDepositionEmissions(@PathVariable UUID id,
            @Valid @RequestBody AtmosphericDepositionEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Atmospheric Deposition emissions updated successfully",
                agricultureEmissionsService.updateAtmosphericNDepositionEmissions(id, dto)));
    }

    @PutMapping("/leachingAndRunoffEmissions/{id}")
    @Operation(summary = "Update leaching and runoff emissions record")
    public ResponseEntity<ApiResponse> updateLeachingAndRunoffEmissions(@PathVariable UUID id,
            @Valid @RequestBody LeachingAndRunoffEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Leaching and Runoff emissions updated successfully",
                agricultureEmissionsService.updateLeachingAndRunoffEmissions(id, dto)));
    }

    @PutMapping("/manureManagementEmissions/{id}")
    @Operation(summary = "Update manure management emissions record")
    public ResponseEntity<ApiResponse> updateManureManagementEmissions(@PathVariable UUID id,
            @Valid @RequestBody ManureManagementEmissionsDto dto) {
        return ResponseEntity.ok(new ApiResponse(true, "Manure Management emissions updated successfully",
                agricultureEmissionsService.updateManureManagementEmissions(id, dto)));
    }

    @DeleteMapping("/aquacultureEmissions/{id}")
    @Operation(summary = "Delete aquaculture emissions record")
    public ResponseEntity<ApiResponse> deleteAquacultureEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteAquacultureEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Aquaculture emissions deleted successfully", null));
    }

    @GetMapping("/aquacultureEmissions/template")
    @Operation(summary = "Download Aquaculture Emissions Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Aquaculture Emissions records")
    public ResponseEntity<byte[]> downloadAquacultureExcelTemplate() {
        byte[] templateBytes = agricultureEmissionsService.generateAquacultureExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Aquaculture_Emissions_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/aquacultureEmissions/excel")
    @Operation(summary = "Upload Aquaculture Emissions records from Excel file", description = "Uploads multiple Aquaculture Emissions records from an Excel file. Records with duplicate year+Activity Description combinations will be skipped.")
    public ResponseEntity<ApiResponse> createAquacultureEmissionsFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = agricultureEmissionsService.createAquacultureEmissionsFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> skippedRows = (List<Map<String, Object>>) result.get("skippedRows");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped.",
                savedCount,
                skippedCount);

        // Optionally add skipped details to the message if there are few skipped
        // records
        if (skippedCount > 0 && skippedRows != null && skippedRows.size() <= 10) {
            StringBuilder skippedDetails = new StringBuilder();
            skippedDetails.append(" Details: ");
            for (Map<String, Object> row : skippedRows) {
                skippedDetails.append(String.format("Row %d (Year: %s, Activity Description: %s, Reason: %s); ",
                        (Integer) row.get("row"),
                        row.get("year"),
                        row.get("activityDesc"),
                        row.get("reason")));
            }
            message += skippedDetails.toString().trim();
        } else if (skippedCount > 0) {
            message += " See response data for details on skipped rows.";
        }

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }

    @DeleteMapping("/entericFermentationEmissions/{id}")
    @Operation(summary = "Delete enteric fermentation emissions record")
    public ResponseEntity<ApiResponse> deleteEntericFermentationEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteEntericFermentationEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Enteric Fermentation emissions deleted successfully", null));
    }

    @DeleteMapping("/limingEmissions/{id}")
    @Operation(summary = "Delete liming emissions record")
    public ResponseEntity<ApiResponse> deleteLimingEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteLimingEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Liming emissions deleted successfully", null));
    }

    @GetMapping("/limingEmissions/template")
    @Operation(summary = "Download Liming Emissions Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Liming Emissions records")
    public ResponseEntity<byte[]> downloadLimingExcelTemplate() {
        byte[] templateBytes = agricultureEmissionsService.generateLimingExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Liming_Emissions_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/limingEmissions/excel")
    @Operation(summary = "Upload Liming Emissions records from Excel file", description = "Uploads multiple Liming Emissions records from an Excel file. Records with duplicate year+Material combinations will be skipped.")
    public ResponseEntity<ApiResponse> createLimingEmissionsFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = agricultureEmissionsService.createLimingEmissionsFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> skippedRows = (List<Map<String, Object>>) result.get("skippedRows");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped.",
                savedCount,
                skippedCount);

        // Optionally add skipped details to the message if there are few skipped
        // records
        if (skippedCount > 0 && skippedRows != null && skippedRows.size() <= 10) {
            StringBuilder skippedDetails = new StringBuilder();
            skippedDetails.append(" Details: ");
            for (Map<String, Object> row : skippedRows) {
                skippedDetails.append(String.format("Row %d (Year: %s, Material: %s, Reason: %s); ",
                        (Integer) row.get("row"),
                        row.get("year"),
                        row.get("material"),
                        row.get("reason")));
            }
            message += skippedDetails.toString().trim();
        } else if (skippedCount > 0) {
            message += " See response data for details on skipped rows.";
        }

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }

    @DeleteMapping("/animalManureAndCompostEmissions/{id}")
    @Operation(summary = "Delete animal manure and compost emissions record")
    public ResponseEntity<ApiResponse> deleteAnimalManureAndCompostEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteAnimalManureAndCompostEmissions(id);
        return ResponseEntity
                .ok(new ApiResponse(true, "Animal Manure and Compost emissions deleted successfully", null));
    }

    @DeleteMapping("/riceCultivationEmissions/{id}")
    @Operation(summary = "Delete rice cultivation emissions record")
    public ResponseEntity<ApiResponse> deleteRiceCultivationEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteRiceCultivationEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Rice Cultivation emissions deleted successfully", null));
    }

    @DeleteMapping("/syntheticFertilizerEmissions/{id}")
    @Operation(summary = "Delete synthetic fertilizer emissions record")
    public ResponseEntity<ApiResponse> deleteSyntheticFertilizerEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteSyntheticFertilizerEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Synthetic Fertilizer emissions deleted successfully", null));
    }

    @DeleteMapping("/ureaEmissions/{id}")
    @Operation(summary = "Delete urea emissions record")
    public ResponseEntity<ApiResponse> deleteUreaEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteUreaEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Urea emissions deleted successfully", null));
    }

    @GetMapping("/ureaEmissions/template")
    @Operation(summary = "Download Urea Emissions Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Urea Emissions records")
    public ResponseEntity<byte[]> downloadUreaExcelTemplate() {
        byte[] templateBytes = agricultureEmissionsService.generateUreaExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Urea_Emissions_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/ureaEmissions/excel")
    @Operation(summary = "Upload Urea Emissions records from Excel file", description = "Uploads multiple Urea Emissions records from an Excel file. Records with duplicate year+Fertilizer Name combinations will be skipped.")
    public ResponseEntity<ApiResponse> createUreaEmissionsFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = agricultureEmissionsService.createUreaEmissionsFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> skippedRows = (List<Map<String, Object>>) result.get("skippedRows");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped.",
                savedCount,
                skippedCount);

        // Optionally add skipped details to the message if there are few skipped
        // records
        if (skippedCount > 0 && skippedRows != null && skippedRows.size() <= 10) {
            StringBuilder skippedDetails = new StringBuilder();
            skippedDetails.append(" Details: ");
            for (Map<String, Object> row : skippedRows) {
                skippedDetails.append(String.format("Row %d (Year: %s, Fertilizer Name: %s, Reason: %s); ",
                        (Integer) row.get("row"),
                        row.get("year"),
                        row.get("fertilizerName"),
                        row.get("reason")));
            }
            message += skippedDetails.toString().trim();
        } else if (skippedCount > 0) {
            message += " See response data for details on skipped rows.";
        }

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }

    @DeleteMapping("/burningEmissions/{id}")
    @Operation(summary = "Delete burning emissions record")
    public ResponseEntity<ApiResponse> deleteBurningEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteBurningEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Burning emissions deleted successfully", null));
    }

    @DeleteMapping("/cropResidueEmissions/{id}")
    @Operation(summary = "Delete crop residue emissions record")
    public ResponseEntity<ApiResponse> deleteCropResidueEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteCropResidueEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Crop Residue emissions deleted successfully", null));
    }

    @DeleteMapping("/pastureExcretionEmissions/{id}")
    @Operation(summary = "Delete pasture excretion emissions record")
    public ResponseEntity<ApiResponse> deletePastureExcretionEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deletePastureExcretionEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Pasture Excretion emissions deleted successfully", null));
    }

    @DeleteMapping("/mineralSoilEmissions/{id}")
    @Operation(summary = "Delete mineral soil emissions record")
    public ResponseEntity<ApiResponse> deleteMineralSoilEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteMineralSoilEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Mineral Soil emissions deleted successfully", null));
    }

    @DeleteMapping("/volatilizationEmissions/{id}")
    @Operation(summary = "Delete volatilization emissions record")
    public ResponseEntity<ApiResponse> deleteVolatilizationEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteVolatilizationEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Volatilization emissions deleted successfully", null));
    }

    @DeleteMapping("/leachingEmissions/{id}")
    @Operation(summary = "Delete leaching emissions record")
    public ResponseEntity<ApiResponse> deleteLeachingEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteLeachingEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Leaching emissions deleted successfully", null));
    }

    @DeleteMapping("/atmosphericNDepositionEmissions/{id}")
    @Operation(summary = "Delete atmospheric N deposition emissions record")
    public ResponseEntity<ApiResponse> deleteAtmosphericNDepositionEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteAtmosphericNDepositionEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Atmospheric Deposition emissions deleted successfully", null));
    }

    @DeleteMapping("/leachingAndRunoffEmissions/{id}")
    @Operation(summary = "Delete leaching and runoff emissions record")
    public ResponseEntity<ApiResponse> deleteLeachingAndRunoffEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteLeachingAndRunoffEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Leaching and Runoff emissions deleted successfully", null));
    }

    @DeleteMapping("/manureManagementEmissions/{id}")
    @Operation(summary = "Delete manure management emissions record")
    public ResponseEntity<ApiResponse> deleteManureManagementEmissions(@PathVariable UUID id) {
        agricultureEmissionsService.deleteManureManagementEmissions(id);
        return ResponseEntity.ok(new ApiResponse(true, "Manure Management emissions deleted successfully", null));
    }

    @GetMapping("/manureManagementEmissions/template")
    @Operation(summary = "Download Manure Management Emissions Excel template", description = "Downloads an Excel template file with the required column headers and data validation for uploading Manure Management Emissions records")
    public ResponseEntity<byte[]> downloadManureManagementExcelTemplate() {
        byte[] templateBytes = agricultureEmissionsService.generateManureManagementExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Manure_Management_Emissions_Template.xlsx");
        headers.setContentLength(templateBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateBytes);
    }

    @PostMapping("/manureManagementEmissions/excel")
    @Operation(summary = "Upload Manure Management Emissions records from Excel file", description = "Uploads multiple Manure Management Emissions records from an Excel file. Records with duplicate year+Species combinations will be skipped.")
    public ResponseEntity<ApiResponse> createManureManagementEmissionsFromExcel(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = agricultureEmissionsService.createManureManagementEmissionsFromExcel(file);

        int savedCount = (Integer) result.get("savedCount");
        int skippedCount = (Integer) result.get("skippedCount");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> skippedRows = (List<Map<String, Object>>) result.get("skippedRows");

        String message = String.format(
                "Upload completed. %d record(s) saved successfully. %d record(s) skipped.",
                savedCount,
                skippedCount);

        // Optionally add skipped details to the message if there are few skipped
        // records
        if (skippedCount > 0 && skippedRows != null && skippedRows.size() <= 10) {
            StringBuilder skippedDetails = new StringBuilder();
            skippedDetails.append(" Details: ");
            for (Map<String, Object> row : skippedRows) {
                skippedDetails.append(String.format("Row %d (Year: %s, Species: %s, Reason: %s); ",
                        (Integer) row.get("row"),
                        row.get("year"),
                        row.get("species"),
                        row.get("reason")));
            }
            message += skippedDetails.toString().trim();
        } else if (skippedCount > 0) {
            message += " See response data for details on skipped rows.";
        }

        return ResponseEntity.ok(new ApiResponse(true, message, result));
    }

    @Operation(summary = "Get Agriculture dashboard summary", description = "Retrieves agriculture emissions summary from all 7 modules.")
    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse> getAgricultureDashboardSummary(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "Agriculture dashboard summary fetched successfully",
                        agricultureEmissionsService.getAgricultureDashboardSummary(startingYear, endingYear)));
    }

    @Operation(summary = "Get Agriculture dashboard graph", description = "Retrieves agriculture emissions graph data by year.")
    @GetMapping("/dashboard/graph")
    public ResponseEntity<ApiResponse> getAgricultureDashboardGraph(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "Agriculture dashboard graph fetched successfully",
                        agricultureEmissionsService.getAgricultureDashboardGraph(startingYear, endingYear)));
    }
}
