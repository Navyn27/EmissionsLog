package com.navyn.emissionlog.modules.LandUseEmissions;

import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import com.navyn.emissionlog.modules.LandUseEmissions.Dtos.*;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import com.navyn.emissionlog.utils.DashboardData;
import java.util.List;

public interface LandUseEmissionsService {

        // BiomassGain methods
        BiomassGain createBiomassGain(BiomassGainDto biomassGainDto);

        List<BiomassGain> getAllBiomassGain(Integer year, LandCategory landCategory);

        BiomassGain updateBiomassGain(java.util.UUID id, BiomassGainDto biomassGainDto);

        void deleteBiomassGain(java.util.UUID id);

        // DisturbanceBiomassLoss methods
        DisturbanceBiomassLoss createDisturbanceBiomassLoss(DisturbanceBiomassLossDto disturbanceBiomassLossDto);

        List<DisturbanceBiomassLoss> getAllDisturbanceBiomassLoss(Integer year, LandCategory landCategory);

        DisturbanceBiomassLoss updateDisturbanceBiomassLoss(java.util.UUID id,
                        DisturbanceBiomassLossDto disturbanceBiomassLossDto);

        void deleteDisturbanceBiomassLoss(java.util.UUID id);

        // FirewoodRemovalBiomassLoss methods
        FirewoodRemovalBiomassLoss createFirewoodRemovalBiomassLoss(
                        FirewoodRemovalBiomassLossDto firewoodRemovalBiomassLossDto);

        List<FirewoodRemovalBiomassLoss> getAllFirewoodRemovalBiomassLoss(Integer year, LandCategory landCategory);

        FirewoodRemovalBiomassLoss updateFirewoodRemovalBiomassLoss(java.util.UUID id,
                        FirewoodRemovalBiomassLossDto firewoodRemovalBiomassLossDto);

        void deleteFirewoodRemovalBiomassLoss(java.util.UUID id);

        // HarvestedBiomassLoss methods
        HarvestedBiomassLoss createHarvestedBiomassLoss(HarvestedBiomassLossDto harvestedBiomassLossDto);

        List<HarvestedBiomassLoss> getAllHarvestedBiomassLoss(Integer year, LandCategory landCategory);

        HarvestedBiomassLoss updateHarvestedBiomassLoss(java.util.UUID id,
                        HarvestedBiomassLossDto harvestedBiomassLossDto);

        void deleteHarvestedBiomassLoss(java.util.UUID id);

        // RewettedMineralWetlands methods (no LandCategory filter)
        RewettedMineralWetlands createRewettedMineralWetlands(RewettedMineralWetlandsDto rewettedMineralWetlandsDto);

        List<RewettedMineralWetlands> getAllRewettedMineralWetlands(Integer year);

        RewettedMineralWetlands updateRewettedMineralWetlands(java.util.UUID id,
                        RewettedMineralWetlandsDto rewettedMineralWetlandsDto);

        void deleteRewettedMineralWetlands(java.util.UUID id);

        // Mini Dashboards
        DashboardData getLandUseDashboardSummary(Integer startingYear, Integer endingYear);

        List<DashboardData> getLandUseDashboardGraph(Integer startingYear, Integer endingYear);
}
