package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.service;

import com.navyn.emissionlog.Enums.Mitigation.ZeroTillageConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.repositories.ZeroTillageMitigationRepository;
import com.navyn.emissionlog.utils.Specifications.MitigationSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ZeroTillageMitigationServiceImpl implements ZeroTillageMitigationService {
    
    private final ZeroTillageMitigationRepository repository;
    
    @Override
    public ZeroTillageMitigation createZeroTillageMitigation(ZeroTillageMitigationDto dto) {
        ZeroTillageMitigation mitigation = new ZeroTillageMitigation();
        
        // Map input fields
        mitigation.setYear(dto.getYear());
        mitigation.setAreaUnderZeroTillage(dto.getAreaUnderZeroTillage());
        
        // 1. Calculate Total Carbon Increase in Soil (Tonnes C)
        // Total carbon = Area × Carbon increase in soil
        double totalCarbon = dto.getAreaUnderZeroTillage() * 
            ZeroTillageConstants.CARBON_INCREASE_SOIL.getValue();
        mitigation.setTotalCarbonIncreaseInSoil(totalCarbon);
        
        // 2. Calculate Emissions Savings (Kilotonnes CO2e)
        // Emissions savings = Total carbon × C to CO2 conversion / 1000
        double emissionsSavings = (totalCarbon * 
            ZeroTillageConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setEmissionsSavings(emissionsSavings);
        
        // 3. Calculate Urea Applied (tonnes)
        // Urea applied = Area × Urea application rate
        double ureaApplied = dto.getAreaUnderZeroTillage() * 
            ZeroTillageConstants.UREA_APPLICATION_RATE.getValue();
        mitigation.setUreaApplied(ureaApplied);
        
        // 4. Calculate Emissions from Urea (Tonnes CO2)
        // Emissions from urea = Urea applied × Emission factor from urea
        double emissionsFromUrea = ureaApplied * 
            ZeroTillageConstants.EMISSION_FACTOR_UREA.getValue();
        mitigation.setEmissionsFromUrea(emissionsFromUrea);
        
        // 5. Calculate GHG Emissions Savings (Kilotonnes CO2e) - NET
        // GHG savings = Emissions savings - (Emissions from urea / 1000)
        double ghgSavings = emissionsSavings - (emissionsFromUrea / 1000.0);
        mitigation.setGhgEmissionsSavings(ghgSavings);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<ZeroTillageMitigation> getAllZeroTillageMitigation(Integer year) {
        Specification<ZeroTillageMitigation> spec = 
            Specification.<ZeroTillageMitigation>where(MitigationSpecifications.hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public Optional<ZeroTillageMitigation> getByYear(Integer year) {
        return repository.findByYear(year);
    }
}
