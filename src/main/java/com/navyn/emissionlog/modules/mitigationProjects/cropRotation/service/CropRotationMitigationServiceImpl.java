package com.navyn.emissionlog.modules.mitigationProjects.cropRotation.service;

import com.navyn.emissionlog.Enums.Mitigation.CropRotationConstants;
import com.navyn.emissionlog.modules.mitigationProjects.cropRotation.dtos.CropRotationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.cropRotation.models.CropRotationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.cropRotation.repositories.CropRotationMitigationRepository;
import com.navyn.emissionlog.utils.Specifications.MitigationSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CropRotationMitigationServiceImpl implements CropRotationMitigationService {
    
    private final CropRotationMitigationRepository repository;
    
    @Override
    public CropRotationMitigation createCropRotationMitigation(CropRotationMitigationDto dto) {
        CropRotationMitigation mitigation = new CropRotationMitigation();
        
        // Map input fields
        mitigation.setYear(dto.getYear());
        mitigation.setCroplandUnderCropRotation(dto.getCroplandUnderCropRotation());
        mitigation.setAbovegroundBiomass(dto.getAbovegroundBiomass());
        mitigation.setIncreasedBiomass(dto.getIncreasedBiomass());
        
        // 1. Calculate Total Increased Biomass (tonnes DM/year)
        // Total increased biomass = Cropland × ABG × Increased biomass × (1 + Ratio BGB to AGB)
        double totalIncreasedBiomass = dto.getCroplandUnderCropRotation() * 
            dto.getAbovegroundBiomass() * 
            dto.getIncreasedBiomass() * 
            (1 + CropRotationConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalIncreasedBiomass(totalIncreasedBiomass);
        
        // 2. Calculate Biomass Carbon (tonnes C/year)
        // Biomass carbon = Total increased biomass × Carbon content in dry biomass
        double biomassCarbonIncrease = totalIncreasedBiomass * 
            CropRotationConstants.CARBON_CONTENT_DRY_BIOMASS.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        // 3. Calculate Mitigated Emissions (Kt CO2e)
        // Mitigated emissions = Biomass carbon × Conversion C to CO2 / 1000
        double mitigatedEmissions = (biomassCarbonIncrease * 
            CropRotationConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<CropRotationMitigation> getAllCropRotationMitigation(Integer year) {
        Specification<CropRotationMitigation> spec = 
            Specification.<CropRotationMitigation>where(MitigationSpecifications.hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public Optional<CropRotationMitigation> getByYear(Integer year) {
        return repository.findByYear(year);
    }
}
