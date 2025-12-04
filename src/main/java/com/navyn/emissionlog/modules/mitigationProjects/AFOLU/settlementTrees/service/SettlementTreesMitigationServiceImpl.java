package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.service;

import com.navyn.emissionlog.Enums.Mitigation.SettlementTreesConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.repositories.SettlementTreesMitigationRepository;
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
public class SettlementTreesMitigationServiceImpl implements SettlementTreesMitigationService {
    
    private final SettlementTreesMitigationRepository repository;
    
    @Override
    public SettlementTreesMitigation createSettlementTreesMitigation(SettlementTreesMitigationDto dto) {
        SettlementTreesMitigation mitigation = new SettlementTreesMitigation();
        Optional<SettlementTreesMitigation> lastYearRecord = repository.findTopByYearLessThanOrderByYearDesc(dto.getYear());
        Double cumulativeNumberOfTrees = lastYearRecord.map(settlementTreesMitigation -> settlementTreesMitigation.getNumberOfTreesPlanted() + settlementTreesMitigation.getCumulativeNumberOfTrees()).orElse(0.0);
        Double agbSingleTreePrevYear = lastYearRecord.map(SettlementTreesMitigation::getAgbSingleTreeCurrentYear).orElse(0.0);

        // Convert AGB to cubic meters (standard unit)
        double agbCurrentYearInCubicMeters = dto.getAgbUnit().toCubicMeters(dto.getAgbSingleTreeCurrentYear());

        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCumulativeNumberOfTrees(cumulativeNumberOfTrees);
        mitigation.setNumberOfTreesPlanted(dto.getNumberOfTreesPlanted());
        mitigation.setAgbSingleTreePreviousYear(agbSingleTreePrevYear);
        mitigation.setAgbSingleTreeCurrentYear(agbCurrentYearInCubicMeters);
        
        // 1. Calculate AGB Growth (tonnes m3)
        // AGB growth = AGB of single tree in current year - AGB of single tree in previous year
        double agbGrowth = agbCurrentYearInCubicMeters - agbSingleTreePrevYear;
        mitigation.setAgbGrowth(agbGrowth);
        
        // 2. Calculate Aboveground Biomass Growth (tonnes DM)
        // Aboveground Biomass Growth = Conversion m3 to tonnes DM × AGB growth × Cumulative number of trees
        double abovegroundBiomassGrowth = 
            SettlementTreesConstants.CONVERSION_M3_TO_TONNES_DM.getValue() * 
            agbGrowth * 
            cumulativeNumberOfTrees;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        // 3. Calculate Total Biomass (tonnes DM/year) - includes belowground
        // Total biomass = Aboveground Biomass Growth × (1 + Ratio BGB to AGB)
        double totalBiomass = abovegroundBiomassGrowth * 
            (1 + SettlementTreesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalBiomass(totalBiomass);
        
        // 4. Calculate Biomass Carbon Increase (tonnes C/year)
        // Biomass carbon increase = Total biomass × Carbon content in dry wood
        double biomassCarbonIncrease = totalBiomass * 
            SettlementTreesConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        // 5. Calculate Mitigated Emissions (Kt CO2e)
        // Mitigated emissions = (Biomass carbon increase × Conversion C to CO2) / 1000
        double mitigatedEmissions = (biomassCarbonIncrease * 
            SettlementTreesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public SettlementTreesMitigation updateSettlementTreesMitigation(UUID id, SettlementTreesMitigationDto dto) {
        SettlementTreesMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Settlement Trees Mitigation record not found with id: " + id));
        
        // Update the current record
        recalculateAndUpdateRecord(mitigation, dto);
        SettlementTreesMitigation updatedRecord = repository.save(mitigation);
        
        // CASCADE: Find and recalculate all subsequent years
        List<SettlementTreesMitigation> subsequentRecords = repository
            .findByYearGreaterThanOrderByYearAsc(dto.getYear());
        
        for (SettlementTreesMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
        
        return updatedRecord;
    }

    @Override
    public void deleteSettlementTreesMitigation(UUID id) {
        SettlementTreesMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Settlement Trees Mitigation record not found with id: " + id));

        Integer year = mitigation.getYear();
        repository.delete(mitigation);

        // Recalculate all subsequent years as cumulative fields depend on previous records
        List<SettlementTreesMitigation> subsequentRecords =
            repository.findByYearGreaterThanOrderByYearAsc(year);
        for (SettlementTreesMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
    }
    
    /**
     * Recalculates an existing record based on its current year and stored input values
     */
    private void recalculateExistingRecord(SettlementTreesMitigation mitigation) {
        Optional<SettlementTreesMitigation> lastYearRecord = repository
            .findTopByYearLessThanOrderByYearDesc(mitigation.getYear());
        Double cumulativeNumberOfTrees = lastYearRecord.map(settlementTreesMitigation -> 
            settlementTreesMitigation.getNumberOfTreesPlanted() + settlementTreesMitigation.getCumulativeNumberOfTrees()
        ).orElse(0.0);
        Double agbSingleTreePrevYear = lastYearRecord.map(SettlementTreesMitigation::getAgbSingleTreeCurrentYear).orElse(0.0);

        mitigation.setCumulativeNumberOfTrees(cumulativeNumberOfTrees);
        mitigation.setAgbSingleTreePreviousYear(agbSingleTreePrevYear);
        
        // Recalculate all derived fields using existing AGB value
        double agbCurrentYearInCubicMeters = mitigation.getAgbSingleTreeCurrentYear();
        
        double agbGrowth = agbCurrentYearInCubicMeters - agbSingleTreePrevYear;
        mitigation.setAgbGrowth(agbGrowth);
        
        double abovegroundBiomassGrowth = 
            SettlementTreesConstants.CONVERSION_M3_TO_TONNES_DM.getValue() * 
            agbGrowth * 
            cumulativeNumberOfTrees;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        double totalBiomass = abovegroundBiomassGrowth * 
            (1 + SettlementTreesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalBiomass(totalBiomass);
        
        double biomassCarbonIncrease = totalBiomass * 
            SettlementTreesConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        double mitigatedEmissions = (biomassCarbonIncrease * 
            SettlementTreesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    /**
     * Recalculates a record with new DTO values
     */
    private void recalculateAndUpdateRecord(SettlementTreesMitigation mitigation, SettlementTreesMitigationDto dto) {
        Optional<SettlementTreesMitigation> lastYearRecord = repository.findTopByYearLessThanOrderByYearDesc(dto.getYear());
        Double cumulativeNumberOfTrees = lastYearRecord.map(settlementTreesMitigation -> 
            settlementTreesMitigation.getNumberOfTreesPlanted() + settlementTreesMitigation.getCumulativeNumberOfTrees()
        ).orElse(0.0);
        Double agbSingleTreePrevYear = lastYearRecord.map(SettlementTreesMitigation::getAgbSingleTreeCurrentYear).orElse(0.0);

        // Convert AGB to cubic meters (standard unit)
        double agbCurrentYearInCubicMeters = dto.getAgbUnit().toCubicMeters(dto.getAgbSingleTreeCurrentYear());

        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCumulativeNumberOfTrees(cumulativeNumberOfTrees);
        mitigation.setNumberOfTreesPlanted(dto.getNumberOfTreesPlanted());
        mitigation.setAgbSingleTreePreviousYear(agbSingleTreePrevYear);
        mitigation.setAgbSingleTreeCurrentYear(agbCurrentYearInCubicMeters);
        
        // Recalculate derived fields
        double agbGrowth = agbCurrentYearInCubicMeters - agbSingleTreePrevYear;
        mitigation.setAgbGrowth(agbGrowth);
        
        double abovegroundBiomassGrowth = 
            SettlementTreesConstants.CONVERSION_M3_TO_TONNES_DM.getValue() * 
            agbGrowth * 
            cumulativeNumberOfTrees;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        double totalBiomass = abovegroundBiomassGrowth * 
            (1 + SettlementTreesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalBiomass(totalBiomass);
        
        double biomassCarbonIncrease = totalBiomass * 
            SettlementTreesConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        double mitigatedEmissions = (biomassCarbonIncrease * 
            SettlementTreesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    @Override
    public List<SettlementTreesMitigation> getAllSettlementTreesMitigation(Integer year) {
        Specification<SettlementTreesMitigation> spec = 
            Specification.<SettlementTreesMitigation>where(MitigationSpecifications.hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public Optional<SettlementTreesMitigation> getByYear(Integer year) {
        return repository.findByYear(year);
    }
}
