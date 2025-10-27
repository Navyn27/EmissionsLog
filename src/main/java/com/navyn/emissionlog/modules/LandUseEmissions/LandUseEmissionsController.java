package com.navyn.emissionlog.modules.LandUseEmissions;

import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import com.navyn.emissionlog.modules.LandUseEmissions.Dtos.*;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/landUseEmissions")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class LandUseEmissionsController {
    
    private final LandUseEmissionsService landUseEmissionsService;
    
    // BiomassGain endpoints
    @PostMapping("/biomassGain")
    public ResponseEntity<BiomassGain> createBiomassGain(@Valid @RequestBody BiomassGainDto biomassGainDto) {
        BiomassGain biomassGain = landUseEmissionsService.createBiomassGain(biomassGainDto);
        return ResponseEntity.ok(biomassGain);
    }
    
    @GetMapping("/biomassGain")
    public ResponseEntity<List<BiomassGain>> getAllBiomassGain(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) LandCategory landCategory) {
        List<BiomassGain> biomassGains = landUseEmissionsService.getAllBiomassGain(year, landCategory);
        return ResponseEntity.ok(biomassGains);
    }
    
    // DisturbanceBiomassLoss endpoints
    @PostMapping("/disturbanceBiomassLoss")
    public ResponseEntity<DisturbanceBiomassLoss> createDisturbanceBiomassLoss(@Valid @RequestBody DisturbanceBiomassLossDto disturbanceBiomassLossDto) {
        DisturbanceBiomassLoss disturbanceBiomassLoss = landUseEmissionsService.createDisturbanceBiomassLoss(disturbanceBiomassLossDto);
        return ResponseEntity.ok(disturbanceBiomassLoss);
    }
    
    @GetMapping("/disturbanceBiomassLoss")
    public ResponseEntity<List<DisturbanceBiomassLoss>> getAllDisturbanceBiomassLoss(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) LandCategory landCategory) {
        List<DisturbanceBiomassLoss> disturbanceBiomassLosses = landUseEmissionsService.getAllDisturbanceBiomassLoss(year, landCategory);
        return ResponseEntity.ok(disturbanceBiomassLosses);
    }
    
    // FirewoodRemovalBiomassLoss endpoints
    @PostMapping("/firewoodRemovalBiomassLoss")
    public ResponseEntity<FirewoodRemovalBiomassLoss> createFirewoodRemovalBiomassLoss(@Valid @RequestBody FirewoodRemovalBiomassLossDto firewoodRemovalBiomassLossDto) {
        FirewoodRemovalBiomassLoss firewoodRemovalBiomassLoss = landUseEmissionsService.createFirewoodRemovalBiomassLoss(firewoodRemovalBiomassLossDto);
        return ResponseEntity.ok(firewoodRemovalBiomassLoss);
    }
    
    @GetMapping("/firewoodRemovalBiomassLoss")
    public ResponseEntity<List<FirewoodRemovalBiomassLoss>> getAllFirewoodRemovalBiomassLoss(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) LandCategory landCategory) {
        List<FirewoodRemovalBiomassLoss> firewoodRemovalBiomassLosses = landUseEmissionsService.getAllFirewoodRemovalBiomassLoss(year, landCategory);
        return ResponseEntity.ok(firewoodRemovalBiomassLosses);
    }
    
    // HarvestedBiomassLoss endpoints
    @PostMapping("/harvestedBiomassLoss")
    public ResponseEntity<HarvestedBiomassLoss> createHarvestedBiomassLoss(@Valid @RequestBody HarvestedBiomassLossDto harvestedBiomassLossDto) {
        HarvestedBiomassLoss harvestedBiomassLoss = landUseEmissionsService.createHarvestedBiomassLoss(harvestedBiomassLossDto);
        return ResponseEntity.ok(harvestedBiomassLoss);
    }
    
    @GetMapping("/harvestedBiomassLoss")
    public ResponseEntity<List<HarvestedBiomassLoss>> getAllHarvestedBiomassLoss(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) LandCategory landCategory) {
        List<HarvestedBiomassLoss> harvestedBiomassLosses = landUseEmissionsService.getAllHarvestedBiomassLoss(year, landCategory);
        return ResponseEntity.ok(harvestedBiomassLosses);
    }
    
    // RewettedMineralWetlands endpoints (no LandCategory filter)
    @PostMapping("/rewettedMineralWetlands")
    public ResponseEntity<RewettedMineralWetlands> createRewettedMineralWetlands(@Valid @RequestBody RewettedMineralWetlandsDto rewettedMineralWetlandsDto) {
        RewettedMineralWetlands rewettedMineralWetlands = landUseEmissionsService.createRewettedMineralWetlands(rewettedMineralWetlandsDto);
        return ResponseEntity.ok(rewettedMineralWetlands);
    }
    
    @GetMapping("/rewettedMineralWetlands")
    public ResponseEntity<List<RewettedMineralWetlands>> getAllRewettedMineralWetlands(
            @RequestParam(required = false) Integer year) {
        List<RewettedMineralWetlands> rewettedMineralWetlands = landUseEmissionsService.getAllRewettedMineralWetlands(year);
        return ResponseEntity.ok(rewettedMineralWetlands);
    }
}
