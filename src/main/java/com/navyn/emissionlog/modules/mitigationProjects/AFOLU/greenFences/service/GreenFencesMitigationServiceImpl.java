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

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GreenFencesMitigationServiceImpl implements GreenFencesMitigationService {
    
    private final GreenFencesMitigationRepository repository;
    
    @Override
    public GreenFencesMitigation createGreenFencesMitigation(GreenFencesMitigationDto dto) {
        GreenFencesMitigation mitigation = new GreenFencesMitigation();

        Optional<GreenFencesMitigation> latestYear = repository.findTopByYearLessThanOrderByYearDesc(dto.getYear());
        Double cumulativeHouseholds = latestYear.map(greenFencesMitigation -> greenFencesMitigation.getCumulativeNumberOfHouseholds() + greenFencesMitigation.getNumberOfHouseholdsWith10m2Fence()).orElse(0.0);

        // Convert AGB to tonnes DM (standard unit)
        double agbInTonnesDM = dto.getAgbUnit().toTonnesDM(dto.getAgbOf10m2LiveFence());

        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCumulativeNumberOfHouseholds(cumulativeHouseholds);
        mitigation.setNumberOfHouseholdsWith10m2Fence(dto.getNumberOfHouseholdsWith10m2Fence());
        mitigation.setAgbOf10m2LiveFence(agbInTonnesDM);
        
        // 1. Calculate AGB of 10m3 fence biomass from cumulative households (Tonnes C)
        // AGB fence biomass = AGB of 10m2 fence × Carbon content × Cumulative households
        double agbFenceBiomass = agbInTonnesDM * 
            GreenFencesConstants.CARBON_CONTENT_DRY_AGB.getValue() * 
            cumulativeHouseholds;
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
