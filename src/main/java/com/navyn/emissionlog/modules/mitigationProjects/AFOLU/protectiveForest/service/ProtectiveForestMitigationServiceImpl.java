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

@Service
@RequiredArgsConstructor
public class ProtectiveForestMitigationServiceImpl implements ProtectiveForestMitigationService {
    
    private final ProtectiveForestMitigationRepository repository;
    
    @Override
    public ProtectiveForestMitigation createProtectiveForestMitigation(ProtectiveForestMitigationDto dto) {
        ProtectiveForestMitigation mitigation = new ProtectiveForestMitigation();

        Optional<ProtectiveForestMitigation> lastYearRecord = repository.findTopByYearAndCategoryOrderByCreatedAtDesc(dto.getYear(), dto.getCategory());
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
            .findTopByYearAndCategoryOrderByCreatedAtDesc(dto.getYear(), dto.getCategory())
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
        return repository.findTopByYearAndCategoryOrderByCreatedAtDesc(year, category);
    }
}
