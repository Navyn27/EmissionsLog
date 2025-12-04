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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ZeroTillageMitigationServiceImpl implements ZeroTillageMitigationService {
    
    private final ZeroTillageMitigationRepository repository;
    
    @Override
    public ZeroTillageMitigation createZeroTillageMitigation(ZeroTillageMitigationDto dto) {
        ZeroTillageMitigation mitigation = new ZeroTillageMitigation();
        
        // Convert area to hectares (standard unit)
        double areaInHectares = dto.getAreaUnit().toHectares(dto.getAreaUnderZeroTillage());
        
        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setAreaUnderZeroTillage(areaInHectares);
        
        // 1. Calculate Total Carbon Increase in Soil (Tonnes C)
        // Total carbon = Area × Carbon increase in soil
        double totalCarbon = areaInHectares * 
            ZeroTillageConstants.CARBON_INCREASE_SOIL.getValue();
        mitigation.setTotalCarbonIncreaseInSoil(totalCarbon);
        
        // 2. Calculate Emissions Savings (Kilotonnes CO2e)
        // Emissions savings = Total carbon × C to CO2 conversion / 1000
        double emissionsSavings = (totalCarbon * 
            ZeroTillageConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setEmissionsSavings(emissionsSavings);
        
        // 3. Calculate Urea Applied (tonnes)
        // Urea applied = Area × Urea application rate
        double ureaApplied = areaInHectares * 
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
    public ZeroTillageMitigation updateZeroTillageMitigation(UUID id, ZeroTillageMitigationDto dto) {
        ZeroTillageMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Zero Tillage Mitigation record not found with id: " + id));
        
        // Convert area to hectares (standard unit)
        double areaInHectares = dto.getAreaUnit().toHectares(dto.getAreaUnderZeroTillage());
        
        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setAreaUnderZeroTillage(areaInHectares);
        
        // Recalculate all derived fields
        double totalCarbon = areaInHectares * 
            ZeroTillageConstants.CARBON_INCREASE_SOIL.getValue();
        mitigation.setTotalCarbonIncreaseInSoil(totalCarbon);
        
        double emissionsSavings = (totalCarbon * 
            ZeroTillageConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setEmissionsSavings(emissionsSavings);
        
        double ureaApplied = areaInHectares * 
            ZeroTillageConstants.UREA_APPLICATION_RATE.getValue();
        mitigation.setUreaApplied(ureaApplied);
        
        double emissionsFromUrea = ureaApplied * 
            ZeroTillageConstants.EMISSION_FACTOR_UREA.getValue();
        mitigation.setEmissionsFromUrea(emissionsFromUrea);
        
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
