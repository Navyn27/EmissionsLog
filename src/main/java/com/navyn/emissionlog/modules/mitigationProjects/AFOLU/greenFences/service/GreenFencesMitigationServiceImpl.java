package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.service;

import com.navyn.emissionlog.Enums.Mitigation.GreenFencesConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.models.GreenFencesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.repositories.GreenFencesMitigationRepository;
import com.navyn.emissionlog.utils.Specifications.MitigationSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GreenFencesMitigationServiceImpl implements GreenFencesMitigationService {
    
    private final GreenFencesMitigationRepository repository;
    
    @Override
    public GreenFencesMitigation createGreenFencesMitigation(GreenFencesMitigationDto dto) {
        GreenFencesMitigation mitigation = new GreenFencesMitigation();
        
        // Map input fields
        mitigation.setYear(dto.getYear());
        mitigation.setCumulativeNumberOfHouseholds(dto.getCumulativeNumberOfHouseholds());
        mitigation.setNumberOfHouseholdsWith10m2Fence(dto.getNumberOfHouseholdsWith10m2Fence());
        mitigation.setAgbOf10m2LiveFence(dto.getAgbOf10m2LiveFence());
        
        // 1. Calculate AGB of 10m3 fence biomass from cumulative households (Tonnes C)
        // AGB fence biomass = AGB of 10m2 fence × Carbon content × Cumulative households
        double agbFenceBiomass = dto.getAgbOf10m2LiveFence() * 
            GreenFencesConstants.CARBON_CONTENT_DRY_AGB.getValue() * 
            dto.getCumulativeNumberOfHouseholds();
        mitigation.setAgbFenceBiomassCumulativeHouseholds(agbFenceBiomass);
        
        // 2. Calculate AGB+BGB from cumulative households (Tonnes C)
        // Total biomass = AGB fence biomass × (1 + Ratio BGB to AGB)
        double totalBiomass = agbFenceBiomass * 
            (1 + GreenFencesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setAgbPlusBgbCumulativeHouseholds(totalBiomass);
        
        // 3. Calculate Mitigated Emissions (Kt CO2e)
        // NOTE: Uses AGB only (not total with BGB) for emissions calculation
        // Mitigated emissions = AGB fence biomass × Conversion C to CO2 / 1000
        double mitigatedEmissions = (agbFenceBiomass * 
            GreenFencesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<GreenFencesMitigation> getAllGreenFencesMitigation(Integer year) {
        Specification<GreenFencesMitigation> spec = 
            Specification.<GreenFencesMitigation>where(MitigationSpecifications.hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public Optional<GreenFencesMitigation> getByYear(Integer year) {
        return repository.findByYear(year);
    }
}
