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

@Service
@RequiredArgsConstructor
public class SettlementTreesMitigationServiceImpl implements SettlementTreesMitigationService {
    
    private final SettlementTreesMitigationRepository repository;
    
    @Override
    public SettlementTreesMitigation createSettlementTreesMitigation(SettlementTreesMitigationDto dto) {
        SettlementTreesMitigation mitigation = new SettlementTreesMitigation();
        
        // Map input fields
        mitigation.setYear(dto.getYear());
        mitigation.setCumulativeNumberOfTrees(dto.getCumulativeNumberOfTrees());
        mitigation.setNumberOfTreesPlanted(dto.getNumberOfTreesPlanted());
        mitigation.setAgbSingleTreePreviousYear(dto.getAgbSingleTreePreviousYear());
        mitigation.setAgbSingleTreeCurrentYear(dto.getAgbSingleTreeCurrentYear());
        
        // 1. Calculate AGB Growth (tonnes m3)
        // AGB growth = AGB of single tree in current year - AGB of single tree in previous year
        double agbGrowth = dto.getAgbSingleTreeCurrentYear() - dto.getAgbSingleTreePreviousYear();
        mitigation.setAgbGrowth(agbGrowth);
        
        // 2. Calculate Aboveground Biomass Growth (tonnes DM)
        // Aboveground Biomass Growth = Conversion m3 to tonnes DM × AGB growth × Cumulative number of trees
        double abovegroundBiomassGrowth = 
            SettlementTreesConstants.CONVERSION_M3_TO_TONNES_DM.getValue() * 
            agbGrowth * 
            dto.getCumulativeNumberOfTrees();
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
