package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.service;

import com.navyn.emissionlog.Enums.Mitigation.CropRotationConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models.CropRotationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.repositories.CropRotationMitigationRepository;
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
public class CropRotationMitigationServiceImpl implements CropRotationMitigationService {
    
    private final CropRotationMitigationRepository repository;
    
    @Override
    public CropRotationMitigation createCropRotationMitigation(CropRotationMitigationDto dto) {
        CropRotationMitigation mitigation = new CropRotationMitigation();
        
        // Convert units to standard values
        double croplandInHectares = dto.getCroplandAreaUnit().toHectares(dto.getCroplandUnderCropRotation());
        double abgInTonnesDMPerHA = dto.getAbovegroundBiomassUnit().toTonnesDMPerHA(dto.getAbovegroundBiomass());
        double increasedBiomassInTonnesDMPerHA = dto.getIncreasedBiomassUnit().toTonnesDMPerHA(dto.getIncreasedBiomass());
        
        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCroplandUnderCropRotation(croplandInHectares);
        mitigation.setAbovegroundBiomass(abgInTonnesDMPerHA);
        mitigation.setIncreasedBiomass(increasedBiomassInTonnesDMPerHA);
        
        // 1. Calculate Total Increased Biomass (tonnes DM/year)
        // Total increased biomass = Cropland × ABG × Increased biomass × (1 + Ratio BGB to AGB)
        double totalIncreasedBiomass = croplandInHectares * 
            abgInTonnesDMPerHA * 
            increasedBiomassInTonnesDMPerHA * 
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
    public CropRotationMitigation updateCropRotationMitigation(UUID id, CropRotationMitigationDto dto) {
        CropRotationMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Crop Rotation Mitigation record not found with id: " + id));
        
        // Convert units to standard values
        double croplandInHectares = dto.getCroplandAreaUnit().toHectares(dto.getCroplandUnderCropRotation());
        double abgInTonnesDMPerHA = dto.getAbovegroundBiomassUnit().toTonnesDMPerHA(dto.getAbovegroundBiomass());
        double increasedBiomassInTonnesDMPerHA = dto.getIncreasedBiomassUnit().toTonnesDMPerHA(dto.getIncreasedBiomass());
        
        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCroplandUnderCropRotation(croplandInHectares);
        mitigation.setAbovegroundBiomass(abgInTonnesDMPerHA);
        mitigation.setIncreasedBiomass(increasedBiomassInTonnesDMPerHA);
        
        // Recalculate derived fields
        double totalIncreasedBiomass = croplandInHectares * 
            abgInTonnesDMPerHA * 
            increasedBiomassInTonnesDMPerHA * 
            (1 + CropRotationConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalIncreasedBiomass(totalIncreasedBiomass);
        
        double biomassCarbonIncrease = totalIncreasedBiomass * 
            CropRotationConstants.CARBON_CONTENT_DRY_BIOMASS.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        double mitigatedEmissions = (biomassCarbonIncrease * 
            CropRotationConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
        
        return repository.save(mitigation);
    }

    @Override
    public void deleteCropRotationMitigation(UUID id) {
        CropRotationMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Crop Rotation Mitigation record not found with id: " + id));
        repository.delete(mitigation);
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
