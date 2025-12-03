package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.constants.EPRPlasticWasteConstants;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRPlasticWasteMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models.EPRPlasticWasteMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.repository.EPRPlasticWasteMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class EPRPlasticWasteMitigationServiceImpl implements EPRPlasticWasteMitigationService {
    
    private final EPRPlasticWasteMitigationRepository repository;
    
    @Override
    public EPRPlasticWasteMitigation createEPRPlasticWasteMitigation(EPRPlasticWasteMitigationDto dto) {
        EPRPlasticWasteMitigation mitigation = new EPRPlasticWasteMitigation();
        
        // Convert BAU emissions to standard unit (ktCO₂eq)
        double bauEmissionsInKilotonnes = dto.getBauSolidWasteEmissionsUnit().toKilotonnesCO2e(dto.getBauSolidWasteEmissions());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setBauSolidWasteEmissions(bauEmissionsInKilotonnes);
        mitigation.setPlasticWasteGrowthFactor(dto.getPlasticWasteGrowthFactor());
        mitigation.setRecyclingRateWithEPR(dto.getRecyclingRateWithEPR());
        mitigation.setPlasticWasteBaseTonnesPerYear(dto.getPlasticWasteBaseTonnesPerYear());
        
        // Calculations
        // 1. Plastic Waste (t/year) = Growth Factor × Last year's Plastic Waste
        //    OR use base value if provided or if no previous year exists
        Double plasticWasteTonnesPerYear;
        
        if (dto.getPlasticWasteBaseTonnesPerYear() != null) {
            // User provided base value - use it directly
            plasticWasteTonnesPerYear = dto.getPlasticWasteBaseTonnesPerYear();
        } else {
            // Try to find previous year's data
            Optional<EPRPlasticWasteMitigation> previousYear = repository.findPreviousYear(dto.getYear());
            
            if (previousYear.isPresent()) {
                // Calculate from previous year
                plasticWasteTonnesPerYear = previousYear.get().getPlasticWasteTonnesPerYear() * dto.getPlasticWasteGrowthFactor();
            } else {
                // No previous year and no base provided - error
                throw new IllegalArgumentException(
                    "Plastic Waste Base (t/year) is required for the first year or when no previous year data exists. " +
                    "Please provide 'plasticWasteBaseTonnesPerYear' in the request."
                );
            }
        }
        mitigation.setPlasticWasteTonnesPerYear(plasticWasteTonnesPerYear);
        
        // 2. Recycling Rate (without EPR) (t/year) = Plastic Waste × 0.03
        Double recyclingWithoutEPR = plasticWasteTonnesPerYear * EPRPlasticWasteConstants.RECYCLING_RATE_WITHOUT_EPR.getValue();
        mitigation.setRecyclingWithoutEPRTonnesPerYear(recyclingWithoutEPR);
        
        // 3. Recycled Plastic (t/year) (with EPR) = Plastic Waste × Recycling Rate (with EPR)
        Double recycledPlasticWithEPR = plasticWasteTonnesPerYear * dto.getRecyclingRateWithEPR();
        mitigation.setRecycledPlasticWithEPRTonnesPerYear(recycledPlasticWithEPR);
        
        // 4. Additional Recycling vs. BAU (t/year) = Recycled Plastic (with EPR) - Recycling (without EPR)
        Double additionalRecycling = recycledPlasticWithEPR - recyclingWithoutEPR;
        mitigation.setAdditionalRecyclingVsBAUTonnesPerYear(additionalRecycling);
        
        // 5. GHG Reduction (tCO2eq) = Additional Recycling × Emission Factor
        Double ghgReductionTonnes = additionalRecycling * EPRPlasticWasteConstants.EMISSION_FACTOR.getValue();
        mitigation.setGhgReductionTonnes(ghgReductionTonnes);
        
        // 6. GHG Reduction (ktCO2eq) = GHG Reduction (tCO2eq) / 1000
        Double ghgReductionKilotonnes = ghgReductionTonnes / 1000;
        mitigation.setGhgReductionKilotonnes(ghgReductionKilotonnes);
        
        return repository.save(mitigation);
    }
    
    @Override
    public EPRPlasticWasteMitigation updateEPRPlasticWasteMitigation(UUID id, EPRPlasticWasteMitigationDto dto) {
        EPRPlasticWasteMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("EPR Plastic Waste Mitigation record not found with id: " + id));
        
        // Convert BAU emissions to standard unit (ktCO₂eq)
        double bauEmissionsInKilotonnes = dto.getBauSolidWasteEmissionsUnit().toKilotonnesCO2e(dto.getBauSolidWasteEmissions());
        
        // Update user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setBauSolidWasteEmissions(bauEmissionsInKilotonnes);
        mitigation.setPlasticWasteGrowthFactor(dto.getPlasticWasteGrowthFactor());
        mitigation.setRecyclingRateWithEPR(dto.getRecyclingRateWithEPR());
        mitigation.setPlasticWasteBaseTonnesPerYear(dto.getPlasticWasteBaseTonnesPerYear());
        
        // Recalculate derived fields - Plastic Waste calculation
        Double plasticWasteTonnesPerYear;
        
        if (dto.getPlasticWasteBaseTonnesPerYear() != null) {
            plasticWasteTonnesPerYear = dto.getPlasticWasteBaseTonnesPerYear();
        } else {
            Optional<EPRPlasticWasteMitigation> previousYear = repository.findPreviousYear(dto.getYear());
            
            if (previousYear.isPresent()) {
                plasticWasteTonnesPerYear = previousYear.get().getPlasticWasteTonnesPerYear() * dto.getPlasticWasteGrowthFactor();
            } else {
                throw new IllegalArgumentException(
                    "Plastic Waste Base (t/year) is required for the first year or when no previous year data exists. " +
                    "Please provide 'plasticWasteBaseTonnesPerYear' in the request."
                );
            }
        }
        mitigation.setPlasticWasteTonnesPerYear(plasticWasteTonnesPerYear);
        
        Double recyclingWithoutEPR = plasticWasteTonnesPerYear * EPRPlasticWasteConstants.RECYCLING_RATE_WITHOUT_EPR.getValue();
        mitigation.setRecyclingWithoutEPRTonnesPerYear(recyclingWithoutEPR);
        
        Double recycledPlasticWithEPR = plasticWasteTonnesPerYear * dto.getRecyclingRateWithEPR();
        mitigation.setRecycledPlasticWithEPRTonnesPerYear(recycledPlasticWithEPR);
        
        Double additionalRecycling = recycledPlasticWithEPR - recyclingWithoutEPR;
        mitigation.setAdditionalRecyclingVsBAUTonnesPerYear(additionalRecycling);
        
        Double ghgReductionTonnes = additionalRecycling * EPRPlasticWasteConstants.EMISSION_FACTOR.getValue();
        mitigation.setGhgReductionTonnes(ghgReductionTonnes);
        
        Double ghgReductionKilotonnes = ghgReductionTonnes / 1000;
        mitigation.setGhgReductionKilotonnes(ghgReductionKilotonnes);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<EPRPlasticWasteMitigation> getAllEPRPlasticWasteMitigation(Integer year) {
        Specification<EPRPlasticWasteMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public void deleteEPRPlasticWasteMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("EPR Plastic Waste Mitigation record not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
