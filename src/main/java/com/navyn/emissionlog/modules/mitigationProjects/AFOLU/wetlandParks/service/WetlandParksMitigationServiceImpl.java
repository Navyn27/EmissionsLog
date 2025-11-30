package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.service;

import com.navyn.emissionlog.Enums.Mitigation.WetlandParksConstants;
import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models.WetlandParksMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.repositories.WetlandParksMitigationRepository;
import com.navyn.emissionlog.utils.Specifications.MitigationSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WetlandParksMitigationServiceImpl implements WetlandParksMitigationService {
    
    private final WetlandParksMitigationRepository repository;
    
    @Override
    public WetlandParksMitigation createWetlandParksMitigation(WetlandParksMitigationDto dto) {
        WetlandParksMitigation mitigation = new WetlandParksMitigation();

        Optional<WetlandParksMitigation> lastYearRecord = repository.findTopByYearLessThanAndTreeCategoryOrderByYearDesc(dto.getYear(), dto.getTreeCategory());
        Double cumulativeArea = lastYearRecord.map(wetlandParksMitigation -> wetlandParksMitigation.getAreaPlanted() + wetlandParksMitigation.getCumulativeArea()).orElse(0.0);

        // Convert units to standard values
        double areaPlantedInHectares = dto.getAreaUnit().toHectares(dto.getAreaPlanted());
        double agbInCubicMeterPerHA = dto.getAgbUnit().toCubicMeterPerHA(dto.getAbovegroundBiomassAGB());

        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setTreeCategory(dto.getTreeCategory());
        mitigation.setCumulativeArea(cumulativeArea);
        mitigation.setAreaPlanted(areaPlantedInHectares);
        mitigation.setAbovegroundBiomassAGB(agbInCubicMeterPerHA);

        // 1. Get previous year's AGB for SAME category
        Double previousAGB = getPreviousYearAGB(dto.getYear(), dto.getTreeCategory());
        mitigation.setPreviousYearAGB(previousAGB);
        
        // 2. Calculate AGB Growth (m3/ha)
        // AGB growth = AGB in current year - AGB in previous year
        double agbGrowth = agbInCubicMeterPerHA - previousAGB;
        mitigation.setAgbGrowth(agbGrowth);
        
        // 3. Calculate Aboveground Biomass Growth (tonnes DM/ha)
        // Aboveground Biomass Growth = AGB growth × Conversion m3 to tonnes DM
        double abovegroundBiomassGrowth = agbGrowth * 
            WetlandParksConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        // 4. Calculate Total Biomass (tonnes DM/year)
        // Total biomass = Cumulative area × Aboveground Biomass Growth
        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);
        
        // 5. Calculate Biomass Carbon Increase (tonnes C/year)
        // Biomass carbon increase = Total biomass × Carbon content in dry wood
        double biomassCarbonIncrease = totalBiomass * 
            WetlandParksConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        // 6. Calculate Mitigated Emissions (Kt CO2e)
        // Mitigated emissions = (Biomass carbon increase × Conversion C to CO2) / 1000
        double mitigatedEmissions = (biomassCarbonIncrease * 
            WetlandParksConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public WetlandParksMitigation updateWetlandParksMitigation(UUID id, WetlandParksMitigationDto dto) {
        WetlandParksMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Wetland Parks Mitigation record not found with id: " + id));

        // Update the current record
        recalculateAndUpdateRecord(mitigation, dto);
        WetlandParksMitigation updatedRecord = repository.save(mitigation);
        
        // CASCADE: Find and recalculate all subsequent years for the same tree category
        List<WetlandParksMitigation> subsequentRecords = repository
            .findByYearGreaterThanAndTreeCategoryOrderByYearAsc(dto.getYear(), dto.getTreeCategory());
        
        for (WetlandParksMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
        
        return updatedRecord;
    }
    
    /**
     * Recalculates an existing record based on its current year and stored input values
     */
    private void recalculateExistingRecord(WetlandParksMitigation mitigation) {
        Optional<WetlandParksMitigation> lastYearRecord = repository
            .findTopByYearLessThanAndTreeCategoryOrderByYearDesc(mitigation.getYear(), mitigation.getTreeCategory());
        Double cumulativeArea = lastYearRecord.map(wetlandParksMitigation -> 
            wetlandParksMitigation.getAreaPlanted() + wetlandParksMitigation.getCumulativeArea()
        ).orElse(0.0);

        mitigation.setCumulativeArea(cumulativeArea);
        
        // Fetch previous year's AGB
        Double previousAGB = getPreviousYearAGB(mitigation.getYear(), mitigation.getTreeCategory());
        mitigation.setPreviousYearAGB(previousAGB);
        
        // Recalculate all derived fields using existing AGB value
        double agbInCubicMeterPerHA = mitigation.getAbovegroundBiomassAGB();
        
        double agbGrowth = agbInCubicMeterPerHA - previousAGB;
        mitigation.setAgbGrowth(agbGrowth);
        
        double abovegroundBiomassGrowth = agbGrowth * 
            WetlandParksConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);
        
        double biomassCarbonIncrease = totalBiomass * 
            WetlandParksConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        double mitigatedEmissions = (biomassCarbonIncrease * 
            WetlandParksConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    /**
     * Recalculates a record with new DTO values
     */
    private void recalculateAndUpdateRecord(WetlandParksMitigation mitigation, WetlandParksMitigationDto dto) {
        Optional<WetlandParksMitigation> lastYearRecord = repository
            .findTopByYearLessThanAndTreeCategoryOrderByYearDesc(dto.getYear(), dto.getTreeCategory());
        Double cumulativeArea = lastYearRecord.map(wetlandParksMitigation -> 
            wetlandParksMitigation.getAreaPlanted() + wetlandParksMitigation.getCumulativeArea()
        ).orElse(0.0);

        // Convert units to standard values
        double areaPlantedInHectares = dto.getAreaUnit().toHectares(dto.getAreaPlanted());
        double agbInCubicMeterPerHA = dto.getAgbUnit().toCubicMeterPerHA(dto.getAbovegroundBiomassAGB());

        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setTreeCategory(dto.getTreeCategory());
        mitigation.setCumulativeArea(cumulativeArea);
        mitigation.setAreaPlanted(areaPlantedInHectares);
        mitigation.setAbovegroundBiomassAGB(agbInCubicMeterPerHA);

        // Recalculate derived fields
        Double previousAGB = getPreviousYearAGB(dto.getYear(), dto.getTreeCategory());
        mitigation.setPreviousYearAGB(previousAGB);
        
        double agbGrowth = agbInCubicMeterPerHA - previousAGB;
        mitigation.setAgbGrowth(agbGrowth);
        
        double abovegroundBiomassGrowth = agbGrowth * 
            WetlandParksConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);
        
        double biomassCarbonIncrease = totalBiomass * 
            WetlandParksConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        double mitigatedEmissions = (biomassCarbonIncrease * 
            WetlandParksConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    @Override
    public List<WetlandParksMitigation> getAllWetlandParksMitigation(
            Integer year, WetlandTreeCategory category) {
        Specification<WetlandParksMitigation> spec = 
            Specification.<WetlandParksMitigation>where(MitigationSpecifications.hasYear(year))
                .and(MitigationSpecifications.hasWetlandTreeCategory(category));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public Optional<WetlandParksMitigation> getByYearAndCategory(
            Integer year, WetlandTreeCategory category) {
        return repository.findByYearAndTreeCategory(year, category);
    }

    // Helper method to get previous year's AGB for same category
    private Double getPreviousYearAGB(Integer currentYear, WetlandTreeCategory category) {
        Optional<WetlandParksMitigation> previous = repository
            .findByYearAndTreeCategory(currentYear - 1, category);
        return previous.map(WetlandParksMitigation::getAbovegroundBiomassAGB).orElse(0.0);
    }
}
