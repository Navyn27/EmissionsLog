package com.navyn.emissionlog.modules.LandUseEmissions;

import com.navyn.emissionlog.Enums.GWP;
import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import com.navyn.emissionlog.Enums.LandUse.LandUseConstants;
import com.navyn.emissionlog.modules.LandUseEmissions.Dtos.*;
import com.navyn.emissionlog.modules.LandUseEmissions.Repositories.*;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.navyn.emissionlog.utils.Specifications.LandUseEmissionsSpecification.*;

@Service
@RequiredArgsConstructor
public class LandUseEmissionsServiceImpl implements LandUseEmissionsService {
    
    private final BiomassGainRepository biomassGainRepository;
    private final DisturbanceBiomassLossRepository disturbanceBiomassLossRepository;
    private final FirewoodRemovalBiomassLossRepository firewoodRemovalBiomassLossRepository;
    private final HarvestedBiomassLossRepository harvestedBiomassLossRepository;
    private final RewettedMineralWetlandsRepository rewettedMineralWetlandsRepository;

    @Override
    public BiomassGain createBiomassGain(BiomassGainDto biomassGainDto) {

        double total_AGB_BGB = LandUseConstants.AVG_ANNUAL_ABG_BIOMASS_GROWTH.getValue() * (1.0 + LandUseConstants.RATIO_BGB_AGB.getValue());

        BiomassGain biomassGain = new BiomassGain();
        biomassGain.setYear(biomassGainDto.getYear());
        biomassGain.setLandCategory(biomassGainDto.getLandCategory());
        biomassGain.setForestArea(biomassGainDto.getForestArea());
        biomassGain.setTotalBiomassGrowth(total_AGB_BGB + biomassGainDto.getForestArea());
        biomassGain.setIncreaseOfBiomassCarbon( (total_AGB_BGB + biomassGainDto.getForestArea()) * LandUseConstants.C_FRACT_DRY_MATTER.getValue());
        biomassGain.setCO2EqOfBiomassCarbonGained(biomassGain.getCO2EqOfBiomassCarbonGained() * LandUseConstants.C_TO_CO2_FACTOR.getValue());
        return biomassGainRepository.save(biomassGain);
    }
    
    @Override
    public List<BiomassGain> getAllBiomassGain(Integer year, LandCategory landCategory) {
        Specification<BiomassGain> spec = Specification.<BiomassGain>where(hasYear(year))
                .and(hasLandCategory(landCategory));
        return biomassGainRepository.findAll(spec);
    }
    
    @Override
    public DisturbanceBiomassLoss createDisturbanceBiomassLoss(DisturbanceBiomassLossDto disturbanceBiomassLossDto) {
        DisturbanceBiomassLoss disturbanceBiomassLoss = new DisturbanceBiomassLoss();
        disturbanceBiomassLoss.setYear(disturbanceBiomassLossDto.getYear());
        disturbanceBiomassLoss.setLandCategory(disturbanceBiomassLossDto.getLandCategory());
        disturbanceBiomassLoss.setForestArea(disturbanceBiomassLossDto.getAffectedForestArea());
        disturbanceBiomassLoss.setAreaAffectedByDisturbance(disturbanceBiomassLossDto.getAreaAffectedByDisturbance());

        // Calculate lossOfBiomassCarbon
        disturbanceBiomassLoss.setLossOfBiomassCarbon(disturbanceBiomassLossDto.getAreaAffectedByDisturbance() * LandUseConstants.ABG_BIOMASS_STOCK.getValue() * (LandUseConstants.RATIO_BGB_AGB.getValue() + 1) * LandUseConstants.C_FRACT_DRY_MATTER.getValue() * LandUseConstants.FRACT_BIOMASS_LOST_DISTURBANCE.getValue());
        disturbanceBiomassLoss.setCO2EqOfBiomassCarbonLoss(disturbanceBiomassLoss.getLossOfBiomassCarbon()* LandUseConstants.C_TO_CO2_FACTOR.getValue());

        return disturbanceBiomassLossRepository.save(disturbanceBiomassLoss);
    }
    
    @Override
    public List<DisturbanceBiomassLoss> getAllDisturbanceBiomassLoss(Integer year, LandCategory landCategory) {
        Specification<DisturbanceBiomassLoss> spec = Specification.<DisturbanceBiomassLoss>where(hasYear(year))
                .and(hasLandCategory(landCategory));
        return disturbanceBiomassLossRepository.findAll(spec);
    }
    
    @Override
    public FirewoodRemovalBiomassLoss createFirewoodRemovalBiomassLoss(FirewoodRemovalBiomassLossDto firewoodRemovalBiomassLossDto) {

        double total_AGB_BGB = LandUseConstants.BIOMASS_CONVERSION_EXPANSION_FACTOR.getValue() * (1.0 + LandUseConstants.RATIO_BGB_AGB.getValue());

        FirewoodRemovalBiomassLoss firewoodRemovalBiomassLoss = new FirewoodRemovalBiomassLoss();
        firewoodRemovalBiomassLoss.setYear(firewoodRemovalBiomassLossDto.getYear());
        firewoodRemovalBiomassLoss.setLandCategory(firewoodRemovalBiomassLossDto.getLandCategory());
        firewoodRemovalBiomassLoss.setRemovedFirewoodAmount(firewoodRemovalBiomassLossDto.getRemovedFirewoodAmount());

        // Calculate lossOfBiomassCarbon
        firewoodRemovalBiomassLoss.setTotalBiomass(total_AGB_BGB * firewoodRemovalBiomassLossDto.getRemovedFirewoodAmount());
        firewoodRemovalBiomassLoss.setLossOfBiomassCarbon(firewoodRemovalBiomassLoss.getTotalBiomass() * LandUseConstants.C_FRACT_DRY_MATTER.getValue() );
        firewoodRemovalBiomassLoss.setCO2EqOfBiomassCarbonLoss(firewoodRemovalBiomassLoss.getLossOfBiomassCarbon() * LandUseConstants.C_TO_CO2_FACTOR.getValue());
        return firewoodRemovalBiomassLossRepository.save(firewoodRemovalBiomassLoss);
    }
    
    @Override
    public List<FirewoodRemovalBiomassLoss> getAllFirewoodRemovalBiomassLoss(Integer year, LandCategory landCategory) {
        Specification<FirewoodRemovalBiomassLoss> spec = Specification.<FirewoodRemovalBiomassLoss>where(hasYear(year))
                .and(hasLandCategory(landCategory));
        return firewoodRemovalBiomassLossRepository.findAll(spec);
    }
    
    @Override
    public HarvestedBiomassLoss createHarvestedBiomassLoss(HarvestedBiomassLossDto harvestedBiomassLossDto) {

        double total_AGB_BGB = LandUseConstants.BIOMASS_CONVERSION_EXPANSION_FACTOR.getValue() * (1.0 + LandUseConstants.RATIO_BGB_AGB.getValue());

        HarvestedBiomassLoss harvestedBiomassLoss = new HarvestedBiomassLoss();
        harvestedBiomassLoss.setYear(harvestedBiomassLossDto.getYear());
        harvestedBiomassLoss.setLandCategory(harvestedBiomassLossDto.getLandCategory());
        harvestedBiomassLoss.setHarvestedWood(harvestedBiomassLossDto.getHarvestedwood());

        // Calculate lossOfBiomassCarbon
        harvestedBiomassLoss.setTotalBiomass(total_AGB_BGB * harvestedBiomassLossDto.getHarvestedwood());
        harvestedBiomassLoss.setLossOfBiomassCarbon(harvestedBiomassLoss.getTotalBiomass() * LandUseConstants.C_FRACT_DRY_MATTER.getValue());
        harvestedBiomassLoss.setCO2EqOfBiomassCarbonLoss(harvestedBiomassLoss.getLossOfBiomassCarbon() * LandUseConstants.C_TO_CO2_FACTOR.getValue());
        return harvestedBiomassLossRepository.save(harvestedBiomassLoss);
    }
    
    @Override
    public List<HarvestedBiomassLoss> getAllHarvestedBiomassLoss(Integer year, LandCategory landCategory) {
        Specification<HarvestedBiomassLoss> spec = Specification.<HarvestedBiomassLoss>where(hasYear(year))
                .and(hasLandCategory(landCategory));
        return harvestedBiomassLossRepository.findAll(spec);
    }
    
    @Override
    public RewettedMineralWetlands createRewettedMineralWetlands(RewettedMineralWetlandsDto rewettedMineralWetlandsDto) {
        RewettedMineralWetlands rewettedMineralWetlands = new RewettedMineralWetlands();
        rewettedMineralWetlands.setYear(rewettedMineralWetlandsDto.getYear());
        rewettedMineralWetlands.setAreaOfRewettedWetlands(rewettedMineralWetlandsDto.getAreaOfRewettedWetlands());

        // Calculate CH4Emissions and CO2EqEmissions
        rewettedMineralWetlands.setCH4Emissions( rewettedMineralWetlandsDto.getAreaOfRewettedWetlands() * LandUseConstants.CH4_EF_REWETTED_LAND.getValue()/1000000);
        rewettedMineralWetlands.setCO2EqEmissions(rewettedMineralWetlands.getCH4Emissions() * GWP.CH4.getValue());
        return rewettedMineralWetlandsRepository.save(rewettedMineralWetlands);
    }
    
    @Override
    public List<RewettedMineralWetlands> getAllRewettedMineralWetlands(Integer year) {
        Specification<RewettedMineralWetlands> spec = Specification.<RewettedMineralWetlands>where(hasYear(year));
        return rewettedMineralWetlandsRepository.findAll(spec);
    }
}
