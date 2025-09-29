package com.navyn.emissionlog.modules.LandUseEmissions;

import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import com.navyn.emissionlog.modules.LandUseEmissions.Dtos.*;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import java.util.List;

public interface LandUseEmissionsService {
    
    // BiomassGain methods
    BiomassGain createBiomassGain(BiomassGainDto biomassGainDto);
    List<BiomassGain> getAllBiomassGain(Integer year, LandCategory landCategory);
    
    // DisturbanceBiomassLoss methods
    DisturbanceBiomassLoss createDisturbanceBiomassLoss(DisturbanceBiomassLossDto disturbanceBiomassLossDto);
    List<DisturbanceBiomassLoss> getAllDisturbanceBiomassLoss(Integer year, LandCategory landCategory);
    
    // FirewoodRemovalBiomassLoss methods
    FirewoodRemovalBiomassLoss createFirewoodRemovalBiomassLoss(FirewoodRemovalBiomassLossDto firewoodRemovalBiomassLossDto);
    List<FirewoodRemovalBiomassLoss> getAllFirewoodRemovalBiomassLoss(Integer year, LandCategory landCategory);
    
    // HarvestedBiomassLoss methods
    HarvestedBiomassLoss createHarvestedBiomassLoss(HarvestedBiomassLossDto harvestedBiomassLossDto);
    List<HarvestedBiomassLoss> getAllHarvestedBiomassLoss(Integer year, LandCategory landCategory);
    
    // RewettedMineralWetlands methods (no LandCategory filter)
    RewettedMineralWetlands createRewettedMineralWetlands(RewettedMineralWetlandsDto rewettedMineralWetlandsDto);
    List<RewettedMineralWetlands> getAllRewettedMineralWetlands(Integer year);
}
