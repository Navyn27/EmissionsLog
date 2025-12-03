package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.service;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models.ProtectiveForestMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.repositories.ProtectiveForestMitigationRepository;
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
public class ProtectiveForestMitigationServiceImpl implements ProtectiveForestMitigationService {
    
    private final ProtectiveForestMitigationRepository repository;
    
    @Override
    public ProtectiveForestMitigation createProtectiveForestMitigation(ProtectiveForestMitigationDto dto) {
        ProtectiveForestMitigation mitigation = new ProtectiveForestMitigation();

        Optional<ProtectiveForestMitigation> lastYearRecord = repository.findTopByYearLessThanAndCategoryOrderByYearDesc(dto.getYear(), dto.getCategory());
        Double cumulativeArea = lastYearRecord.map(protectiveForestMitigation -> protectiveForestMitigation.getCumulativeArea() + protectiveForestMitigation.getAreaPlanted()).orElse(0.0);
        
        // Convert units to standard values
        double areaPlantedInHectares = dto.getAreaPlantedUnit().toHectares(dto.getAreaPlanted());
        double agbCurrentYearInCubicMeterPerHA = dto.getAgbUnit().toCubicMeterPerHA(dto.getAgbCurrentYear());
        
        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCategory(dto.getCategory());
        mitigation.setCumulativeArea(cumulativeArea);
        mitigation.setAreaPlanted(areaPlantedInHectares);
        mitigation.setAgbCurrentYear(agbCurrentYearInCubicMeterPerHA);
        
        // AUTO-FETCH previous year's AGB from DB
        double previousYearAGB = repository
            .findTopByYearLessThanAndCategoryOrderByYearDesc(dto.getYear(), dto.getCategory())
            .map(ProtectiveForestMitigation::getAgbCurrentYear)
            .orElse(0.0);
        
        mitigation.setAgbPreviousYear(previousYearAGB);
        
        // 1. Calculate AGB Growth (tonnes m3/ha)
        double agbGrowth = agbCurrentYearInCubicMeterPerHA - previousYearAGB;
        mitigation.setAgbGrowth(agbGrowth);
        
        // 2. Calculate Aboveground Biomass Growth (tonnes DM/ha)
        double abovegroundBiomassGrowth = agbGrowth * 
            ProtectiveForestConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        // 3. Calculate Total Biomass (tonnes DM/year)
        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);
        
        // 4. Calculate Biomass Carbon Increase (tonnes C/year)
        double biomassCarbonIncrease = totalBiomass * 
            ProtectiveForestConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        // 5. Calculate Mitigated Emissions (Kt CO2e)
        double mitigatedEmissions = (biomassCarbonIncrease * 
            ProtectiveForestConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public ProtectiveForestMitigation updateProtectiveForestMitigation(UUID id, ProtectiveForestMitigationDto dto) {
        ProtectiveForestMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Protective Forest Mitigation record not found with id: " + id));

        // Update the current record
        recalculateAndUpdateRecord(mitigation, dto);
        ProtectiveForestMitigation updatedRecord = repository.save(mitigation);
        
        // CASCADE: Find and recalculate all subsequent years for the same category
        List<ProtectiveForestMitigation> subsequentRecords = repository
            .findByYearGreaterThanAndCategoryOrderByYearAsc(dto.getYear(), dto.getCategory());
        
        for (ProtectiveForestMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
        
        return updatedRecord;
    }

    @Override
    public void deleteProtectiveForestMitigation(UUID id) {
        ProtectiveForestMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Protective Forest Mitigation record not found with id: " + id));

        Integer year = mitigation.getYear();
        ProtectiveForestCategory category = mitigation.getCategory();
        repository.delete(mitigation);

        // Recalculate all subsequent years for this category because cumulative fields depend on previous records
        List<ProtectiveForestMitigation> subsequentRecords =
            repository.findByYearGreaterThanAndCategoryOrderByYearAsc(year, category);
        for (ProtectiveForestMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
    }
    
    /**
     * Recalculates an existing record based on its current year and stored input values
     */
    private void recalculateExistingRecord(ProtectiveForestMitigation mitigation) {
        Optional<ProtectiveForestMitigation> lastYearRecord = repository
            .findTopByYearLessThanAndCategoryOrderByYearDesc(mitigation.getYear(), mitigation.getCategory());
        Double cumulativeArea = lastYearRecord.map(protectiveForestMitigation -> 
            protectiveForestMitigation.getCumulativeArea() + protectiveForestMitigation.getAreaPlanted()
        ).orElse(0.0);

        mitigation.setCumulativeArea(cumulativeArea);
        
        // AUTO-FETCH previous year's AGB from DB
        double previousYearAGB = repository
            .findTopByYearLessThanAndCategoryOrderByYearDesc(mitigation.getYear(), mitigation.getCategory())
            .map(ProtectiveForestMitigation::getAgbCurrentYear)
            .orElse(0.0);
        
        mitigation.setAgbPreviousYear(previousYearAGB);
        
        // Recalculate all derived fields using existing values
        double agbCurrentYearInCubicMeterPerHA = mitigation.getAgbCurrentYear();
        
        double agbGrowth = agbCurrentYearInCubicMeterPerHA - previousYearAGB;
        mitigation.setAgbGrowth(agbGrowth);
        
        double abovegroundBiomassGrowth = agbGrowth * 
            ProtectiveForestConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);
        
        double biomassCarbonIncrease = totalBiomass * 
            ProtectiveForestConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        double mitigatedEmissions = (biomassCarbonIncrease * 
            ProtectiveForestConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    /**
     * Recalculates a record with new DTO values
     */
    private void recalculateAndUpdateRecord(ProtectiveForestMitigation mitigation, ProtectiveForestMitigationDto dto) {
        Optional<ProtectiveForestMitigation> lastYearRecord = repository
            .findTopByYearLessThanAndCategoryOrderByYearDesc(dto.getYear(), dto.getCategory());
        Double cumulativeArea = lastYearRecord.map(protectiveForestMitigation -> 
            protectiveForestMitigation.getCumulativeArea() + protectiveForestMitigation.getAreaPlanted()
        ).orElse(0.0);
        
        // Convert units to standard values
        double areaPlantedInHectares = dto.getAreaPlantedUnit().toHectares(dto.getAreaPlanted());
        double agbCurrentYearInCubicMeterPerHA = dto.getAgbUnit().toCubicMeterPerHA(dto.getAgbCurrentYear());
        
        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCategory(dto.getCategory());
        mitigation.setCumulativeArea(cumulativeArea);
        mitigation.setAreaPlanted(areaPlantedInHectares);
        mitigation.setAgbCurrentYear(agbCurrentYearInCubicMeterPerHA);
        
        // AUTO-FETCH previous year's AGB from DB
        double previousYearAGB = repository
            .findTopByYearLessThanAndCategoryOrderByYearDesc(dto.getYear(), dto.getCategory())
            .map(ProtectiveForestMitigation::getAgbCurrentYear)
            .orElse(0.0);
        
        mitigation.setAgbPreviousYear(previousYearAGB);
        
        // Recalculate derived fields
        double agbGrowth = agbCurrentYearInCubicMeterPerHA - previousYearAGB;
        mitigation.setAgbGrowth(agbGrowth);
        
        double abovegroundBiomassGrowth = agbGrowth * 
            ProtectiveForestConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);
        
        double biomassCarbonIncrease = totalBiomass * 
            ProtectiveForestConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        double mitigatedEmissions = (biomassCarbonIncrease * 
            ProtectiveForestConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    @Override
    public List<ProtectiveForestMitigation> getAllProtectiveForestMitigation(
            Integer year, 
            ProtectiveForestCategory category) {
        
        Specification<ProtectiveForestMitigation> spec = Specification.where(null);
        
        if (year != null) {
            spec = spec.and(MitigationSpecifications.hasYear(year));
        }
        
        if (category != null) {
            spec = spec.and(MitigationSpecifications.hasProtectiveForestCategory(category));
        }
        
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public Optional<ProtectiveForestMitigation> getByYearAndCategory(
            Integer year, 
            ProtectiveForestCategory category) {
        return repository.findByYearAndCategory(year, category);
    }
}
