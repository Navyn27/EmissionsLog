package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.service;

import com.navyn.emissionlog.Enums.Mitigation.StreetTreesConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.models.StreetTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.repositories.StreetTreesMitigationRepository;
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
public class StreetTreesMitigationServiceImpl implements StreetTreesMitigationService {
    
    private final StreetTreesMitigationRepository repository;

    private static Double apply(StreetTreesMitigation streetTreesMitigation) {
        return streetTreesMitigation.getNumberOfTreesPlanted() + streetTreesMitigation.getCumulativeNumberOfTrees();
    }

    @Override
    public StreetTreesMitigation createStreetTreesMitigation(StreetTreesMitigationDto dto) {
        StreetTreesMitigation mitigation = new StreetTreesMitigation();

        Optional<StreetTreesMitigation> lastYearRecord = repository.findTopByYearLessThanOrderByYearDesc(dto.getYear());
        Double cumulativeNumberOfTrees = lastYearRecord.map(StreetTreesMitigationServiceImpl::apply).orElse(0.0);
        Double agbSingleTreePrevYear = lastYearRecord.map(StreetTreesMitigation::getAgbSingleTreeCurrentYear).orElse(0.0);

        // Convert AGB to cubic meters (standard unit)
        double agbCurrentYearInCubicMeters = dto.getAgbUnit().toCubicMeters(dto.getAgbSingleTreeCurrentYear());

        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCumulativeNumberOfTrees(cumulativeNumberOfTrees);
        mitigation.setNumberOfTreesPlanted(dto.getNumberOfTreesPlanted());
        mitigation.setAgbSingleTreePreviousYear(agbSingleTreePrevYear);
        mitigation.setAgbSingleTreeCurrentYear(agbCurrentYearInCubicMeters);
        
        // 1. Calculate AGB Growth (tonnes m3)
        double agbGrowth = agbCurrentYearInCubicMeters - agbSingleTreePrevYear;
        mitigation.setAgbGrowth(agbGrowth);
        
        // 2. Calculate Aboveground Biomass Growth (tonnes DM)
        double abovegroundBiomassGrowth = 
            StreetTreesConstants.CONVERSION_M3_TO_TONNES_DM.getValue() * 
            agbGrowth * 
            cumulativeNumberOfTrees;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        // 3. Calculate Total Biomass (tonnes DM/year) - includes belowground
        double totalBiomass = abovegroundBiomassGrowth * 
            (1 + StreetTreesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalBiomass(totalBiomass);
        
        // 4. Calculate Biomass Carbon Increase (tonnes C/year)
        double biomassCarbonIncrease = totalBiomass * 
            StreetTreesConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        // 5. Calculate Mitigated Emissions (Kt CO2e)
        double mitigatedEmissions = (biomassCarbonIncrease * 
            StreetTreesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public StreetTreesMitigation updateStreetTreesMitigation(UUID id, StreetTreesMitigationDto dto) {
        StreetTreesMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Street Trees Mitigation record not found with id: " + id));
        
        // Update the current record
        recalculateAndUpdateRecord(mitigation, dto);
        StreetTreesMitigation updatedRecord = repository.save(mitigation);
        
        // CASCADE: Find and recalculate all subsequent years
        List<StreetTreesMitigation> subsequentRecords = repository
            .findByYearGreaterThanOrderByYearAsc(dto.getYear());
        
        for (StreetTreesMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
        
        return updatedRecord;
    }

    @Override
    public void deleteStreetTreesMitigation(UUID id) {
        StreetTreesMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Street Trees Mitigation record not found with id: " + id));

        Integer year = mitigation.getYear();
        repository.delete(mitigation);

        // Recalculate all subsequent years as cumulative fields depend on previous records
        List<StreetTreesMitigation> subsequentRecords =
            repository.findByYearGreaterThanOrderByYearAsc(year);
        for (StreetTreesMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
    }
    
    /**
     * Recalculates an existing record based on its current year and stored input values
     */
    private void recalculateExistingRecord(StreetTreesMitigation mitigation) {
        Optional<StreetTreesMitigation> lastYearRecord = repository
            .findTopByYearLessThanOrderByYearDesc(mitigation.getYear());
        Double cumulativeNumberOfTrees = lastYearRecord.map(StreetTreesMitigationServiceImpl::apply).orElse(0.0);
        Double agbSingleTreePrevYear = lastYearRecord.map(StreetTreesMitigation::getAgbSingleTreeCurrentYear).orElse(0.0);

        mitigation.setCumulativeNumberOfTrees(cumulativeNumberOfTrees);
        mitigation.setAgbSingleTreePreviousYear(agbSingleTreePrevYear);
        
        // Recalculate all derived fields using existing AGB value
        double agbCurrentYearInCubicMeters = mitigation.getAgbSingleTreeCurrentYear();
        
        double agbGrowth = agbCurrentYearInCubicMeters - agbSingleTreePrevYear;
        mitigation.setAgbGrowth(agbGrowth);
        
        double abovegroundBiomassGrowth = 
            StreetTreesConstants.CONVERSION_M3_TO_TONNES_DM.getValue() * 
            agbGrowth * 
            cumulativeNumberOfTrees;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        double totalBiomass = abovegroundBiomassGrowth * 
            (1 + StreetTreesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalBiomass(totalBiomass);
        
        double biomassCarbonIncrease = totalBiomass * 
            StreetTreesConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        double mitigatedEmissions = (biomassCarbonIncrease * 
            StreetTreesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    /**
     * Recalculates a record with new DTO values
     */
    private void recalculateAndUpdateRecord(StreetTreesMitigation mitigation, StreetTreesMitigationDto dto) {
        Optional<StreetTreesMitigation> lastYearRecord = repository.findTopByYearLessThanOrderByYearDesc(dto.getYear());
        Double cumulativeNumberOfTrees = lastYearRecord.map(StreetTreesMitigationServiceImpl::apply).orElse(0.0);
        Double agbSingleTreePrevYear = lastYearRecord.map(StreetTreesMitigation::getAgbSingleTreeCurrentYear).orElse(0.0);

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
            StreetTreesConstants.CONVERSION_M3_TO_TONNES_DM.getValue() * 
            agbGrowth * 
            cumulativeNumberOfTrees;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        double totalBiomass = abovegroundBiomassGrowth * 
            (1 + StreetTreesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalBiomass(totalBiomass);
        
        double biomassCarbonIncrease = totalBiomass * 
            StreetTreesConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        double mitigatedEmissions = (biomassCarbonIncrease * 
            StreetTreesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    @Override
    public List<StreetTreesMitigation> getAllStreetTreesMitigation(Integer year) {
        Specification<StreetTreesMitigation> spec = 
            Specification.<StreetTreesMitigation>where(MitigationSpecifications.hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public Optional<StreetTreesMitigation> getByYear(Integer year) {
        return repository.findByYear(year);
    }
}
