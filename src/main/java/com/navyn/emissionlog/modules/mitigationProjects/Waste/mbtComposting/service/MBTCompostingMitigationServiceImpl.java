package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.constants.MBTCompostingConstants;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.models.MBTCompostingMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.repository.MBTCompostingMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class MBTCompostingMitigationServiceImpl implements MBTCompostingMitigationService {
    
    private final MBTCompostingMitigationRepository repository;
    
    @Override
    public MBTCompostingMitigation createMBTCompostingMitigation(MBTCompostingMitigationDto dto) {
        MBTCompostingMitigation mitigation = new MBTCompostingMitigation();
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setOperationStatus(dto.getOperationStatus());
        mitigation.setOrganicWasteTreatedTonsPerDay(dto.getOrganicWasteTreatedTonsPerDay());
        mitigation.setBauEmissionBiologicalTreatment(dto.getBauEmissionBiologicalTreatment());
        
        // Calculations
        // Organic Waste Treated (tons/year) = Organic Waste Treated (tons/day) * days based on operation status
        // - 365/2 (182.5) if half year
        // - 365 if full year
        // - 0 if construction/pre-op
        Double daysPerYear = dto.getOperationStatus().getDaysPerYear();
        Double organicWasteTreatedTonsPerYear = dto.getOrganicWasteTreatedTonsPerDay() * daysPerYear;
        mitigation.setOrganicWasteTreatedTonsPerYear(organicWasteTreatedTonsPerYear);
        
        // Estimated GHG Reduction (tCO2eq/year) = Emission Factor * Organic Waste Treated (tons/year)
        Double estimatedGhgReductionTonnes = MBTCompostingConstants.EMISSION_FACTOR.getValue() * organicWasteTreatedTonsPerYear;
        mitigation.setEstimatedGhgReductionTonnesPerYear(estimatedGhgReductionTonnes);
        
        // Convert to kilotonnes
        Double estimatedGhgReductionKilotonnes = estimatedGhgReductionTonnes / 1000;
        mitigation.setEstimatedGhgReductionKilotonnesPerYear(estimatedGhgReductionKilotonnes);
        
        // Adjusted BAU Emission Biological Treatment (ktCO2eq/year) = BAU Emission - GHG Reduction (kt)
        Double adjustedBauEmission = dto.getBauEmissionBiologicalTreatment() - estimatedGhgReductionKilotonnes;
        mitigation.setAdjustedBauEmissionBiologicalTreatment(adjustedBauEmission);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<MBTCompostingMitigation> getAllMBTCompostingMitigation(Integer year) {
        Specification<MBTCompostingMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
