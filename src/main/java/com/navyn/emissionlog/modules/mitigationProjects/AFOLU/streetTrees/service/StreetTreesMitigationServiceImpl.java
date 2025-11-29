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
