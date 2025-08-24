package com.navyn.emissionlog.modules.agricultureEmissions;

import com.navyn.emissionlog.Enums.Agriculture.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock.EntericFermentationEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock.ManureMgmtEmissionsDto;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agriculture")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class AgricultureEmissionsController {

    private final AgricultureEmissionsService agricultureEmissionsService;

    //get all liming emissions log
    @GetMapping("/limingEmissions")
    @Operation(summary = "Get all liming emissions")
    private ResponseEntity<ApiResponse> getAllLimingEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "limingMaterial") LimingMaterials limingMaterials) {
        return ResponseEntity.ok(new ApiResponse(true,"Liming emissions fetched successfully", agricultureEmissionsService.getAllLimingEmissions(year, limingMaterials)));
    }


    //get all aquaculture emissions log
    @GetMapping("/aquacultureEmissions")
    @Operation(summary = "Get all aquaculture emissions")
    private ResponseEntity<ApiResponse> getAllAquacultureEmissions(@RequestParam(required = false, value = "year") Integer year) {
        return ResponseEntity.ok(new ApiResponse(true, "Aquaculture emissions fetched successfully", agricultureEmissionsService.getAllAquacultureEmissions(year)));
    }


    //get all synthetic emissions log
    @GetMapping("/syntheticFertilizerEmissions")
    @Operation(summary = "Get all synthetic fertilizer emissions")
    private ResponseEntity<ApiResponse> getAllSyntheticFertilizerEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "fertilizerType") Fertilizers fertilizerType, @RequestParam(required = false, value = "cropType") CropTypes cropType) {
        return ResponseEntity.ok(new ApiResponse(true, "Synthetic fertilizer emissions fetched successfully", agricultureEmissionsService.getAllSyntheticFertilizerEmissions(year, cropType, fertilizerType)));
    }

    //get all animal manure and compost emissions log
    @GetMapping("/ureaEmissions")
    @Operation(summary = "Get all urea emissions")
    private ResponseEntity<ApiResponse> getAllUreaEmissions(@RequestParam(required = false, value = "fertilizerName") String fertilizer, @RequestParam(required = false, value = "year") Integer year) {
        return ResponseEntity.ok(new ApiResponse(true, "Urea emissions fetched successfully", agricultureEmissionsService.getAllUreaEmissions(fertilizer, year)));
    }

    //get all rice cultivation emissions log
    @GetMapping("/riceCultivationEmissions")
    @Operation(summary = "Get all rice cultivation emissions")
    private ResponseEntity<ApiResponse> getAllRiceCultivationEmissions(@RequestParam(required = false, value = "riceEcosystem") String riceEcosystem, @RequestParam(required = false, value = "waterRegime") WaterRegime waterRegime, @RequestParam(required = false, value = "year") Integer year) {
        return ResponseEntity.ok(new ApiResponse(true, "Rice cultivation emissions fetched successfully", agricultureEmissionsService.getAllRiceCultivationEmissions(riceEcosystem, waterRegime, year)));
    }

    //get all manure management emissions log
    @GetMapping("/manureMgmtEmissions")
    @Operation(summary = "Get all manure management emissions")
    private ResponseEntity<ApiResponse> getAllManureMgmtEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "amendmentType") OrganicAmendmentTypes amendmentType, @RequestParam(required = false, value = "species") LivestockSpecies species) {
        return ResponseEntity.ok(new ApiResponse(true, "Manure management emissions fetched successfully", agricultureEmissionsService.getAllManureMgmtEmissions(year, amendmentType, species)));
    }

    //get all enteric fermentation emissions log
    @GetMapping("/entericFermentationEmissions")
    @Operation(summary = "Get all enteric fermentation emissions")
    private ResponseEntity<ApiResponse> getAllEntericFermentationEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "species") LivestockSpecies species) {
        return ResponseEntity.ok(new ApiResponse(true, "Enteric fermentation emissions fetched successfully", agricultureEmissionsService.getAllEntericFermentationEmissions(year, species)));
    }

    //create all liming emissions log
    @PostMapping("/limingEmissions")
    @Operation(summary = "Create new liming emissions record")
    private ResponseEntity<ApiResponse> createLimingEmissions(@RequestBody LimingEmissionsDto limingEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Liming emissions created successfully", agricultureEmissionsService.createLimingEmissions(limingEmissionsDto)));
    }

    //create all urea emissions log
    @PostMapping("/ureaEmissions")
    @Operation(summary = "Create new urea emissions record")
    private ResponseEntity<ApiResponse> createUreaEmissions(@RequestBody UreaEmissionsDto ureaEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Urea emissions created successfully", agricultureEmissionsService.createUreaEmissions(ureaEmissionsDto)));
    }

    //create all aquaculture emissions log
    @PostMapping("/aquacultureEmissions")
    @Operation(summary = "Create new aquaculture emissions record")
    private ResponseEntity<ApiResponse> createAquacultureEmissions(@RequestBody AquacultureEmissionsDto aquacultureEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Aquaculture emissions created successfully", agricultureEmissionsService.createAquacultureEmissions(aquacultureEmissionsDto)));
    }

    //create all synthetic emissions log
    @PostMapping("/syntheticFertilizerEmissions")
    @Operation(summary = "Create new synthetic fertilizer emissions record")
    private ResponseEntity<ApiResponse> createSyntheticFertilizerEmissions(@RequestBody SyntheticFertilizerEmissionsDto syntheticFertilizerEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Synthetic fertilizer emissions created successfully", agricultureEmissionsService.createSyntheticFertilizerEmissions(syntheticFertilizerEmissionsDto)));
    }

    //create all rice cultivation emissions log
    @PostMapping("/riceCultivationEmissions")
    @Operation(summary = "Create new rice cultivation emissions record")
    private ResponseEntity<ApiResponse> createRiceCultivationEmissions(@RequestBody RiceCultivationEmissionsDto riceCultivationEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Rice cultivation emissions created successfully", agricultureEmissionsService.createRiceCultivationEmissions(riceCultivationEmissionsDto)));
    }

    //create manure management emissions log
    @PostMapping("/manureMgmtEmissions")
    @Operation(summary = "Create new manure management emissions record")
    private ResponseEntity<ApiResponse> createManureMgmtEmissions(@RequestBody ManureMgmtEmissionsDto manureMgmtEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Manure management emissions created successfully", agricultureEmissionsService.createManureMgmtEmissions(manureMgmtEmissionsDto)));
    }

    //create enteric fermentation emissions log
    @PostMapping("/entericFermentationEmissions")
    @Operation(summary = "Create new enteric fermentation emissions record")
    private ResponseEntity<ApiResponse> createEntericFermentationEmissions(@RequestBody EntericFermentationEmissionsDto entericFermentationEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Enteric fermentation emissions created successfully", agricultureEmissionsService.createEntericFermentationEmissions(entericFermentationEmissionsDto)));
    }

    //create burning emissions log
    @PostMapping("/burningEmissions")
    @Operation(summary = "Create new burning emissions record")
    private ResponseEntity<ApiResponse> createBurningEmissions(@RequestBody BurningEmissionsDto burningEmissionsDto) {
        return ResponseEntity.ok(new ApiResponse(true, "Burning emissions created successfully", agricultureEmissionsService.createBurningEmissions(burningEmissionsDto)));
    }

    //get all burning emissions logs
    @GetMapping("/burningEmissions")
    @Operation(summary = "Get all burning emissions")
    private ResponseEntity<ApiResponse> getAllBurningEmissions(@RequestParam(required = false, value = "year") Integer year, @RequestParam(required = false, value = "forestType") BurningAgentType forestType) {
        return ResponseEntity.ok(new ApiResponse(true, "Burning emissions fetched successfully", agricultureEmissionsService.getAllBurningEmissions(year, forestType)));
    }


}
