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
        
        // Convert to standard units
        double organicWasteInTonnesPerDay = dto.getOrganicWasteTreatedUnit().toTonnesPerDay(dto.getOrganicWasteTreatedTonsPerDay());
        double bauEmissionInKilotonnes = dto.getBauEmissionUnit().toKilotonnesCO2e(dto.getBauEmissionBiologicalTreatment());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setOperationStatus(dto.getOperationStatus());
        mitigation.setOrganicWasteTreatedTonsPerDay(organicWasteInTonnesPerDay);
        mitigation.setBauEmissionBiologicalTreatment(bauEmissionInKilotonnes);
        
        // Calculations
        // Organic Waste Treated (tons/year) = Organic Waste Treated (tons/day) × days based on operation status
        // - PRE_OPERATION: 0 days
        // - HALF_YEAR_OPERATION: 182.5 days (365/2)
        // - FULL_YEAR_OPERATION: 365 days
        Double daysPerYear = dto.getOperationStatus().getDaysPerYear();
        Double organicWasteTreatedTonsPerYear = organicWasteInTonnesPerDay * daysPerYear;
        mitigation.setOrganicWasteTreatedTonsPerYear(organicWasteTreatedTonsPerYear);
        
        // Estimated GHG Reduction (tCO2eq/year) = Emission Factor * Organic Waste Treated (tons/year)
        Double estimatedGhgReductionTonnes = MBTCompostingConstants.EMISSION_FACTOR.getValue() * organicWasteTreatedTonsPerYear;
        mitigation.setEstimatedGhgReductionTonnesPerYear(estimatedGhgReductionTonnes);
        
        // Convert to kilotonnes
        Double estimatedGhgReductionKilotonnes = estimatedGhgReductionTonnes / 1000;
        mitigation.setEstimatedGhgReductionKilotonnesPerYear(estimatedGhgReductionKilotonnes);
        
        // Adjusted BAU Emission Biological Treatment (ktCO2eq/year) = BAU Emission - GHG Reduction (kt)
        Double adjustedBauEmission = bauEmissionInKilotonnes - estimatedGhgReductionKilotonnes;
        mitigation.setAdjustedBauEmissionBiologicalTreatment(adjustedBauEmission);
        
        return repository.save(mitigation);
    }
    
    @Override
    public MBTCompostingMitigation updateMBTCompostingMitigation(Long id, MBTCompostingMitigationDto dto) {
        MBTCompostingMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("MBT Composting Mitigation record not found with id: " + id));
        
        // Convert to standard units
        double organicWasteInTonnesPerDay = dto.getOrganicWasteTreatedUnit().toTonnesPerDay(dto.getOrganicWasteTreatedTonsPerDay());
        double bauEmissionInKilotonnes = dto.getBauEmissionUnit().toKilotonnesCO2e(dto.getBauEmissionBiologicalTreatment());
        
        // Update user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setOperationStatus(dto.getOperationStatus());
        mitigation.setOrganicWasteTreatedTonsPerDay(organicWasteInTonnesPerDay);
        mitigation.setBauEmissionBiologicalTreatment(bauEmissionInKilotonnes);
        
        // Recalculate derived fields
        Double daysPerYear = dto.getOperationStatus().getDaysPerYear();
        Double organicWasteTreatedTonsPerYear = organicWasteInTonnesPerDay * daysPerYear;
        mitigation.setOrganicWasteTreatedTonsPerYear(organicWasteTreatedTonsPerYear);
        
        Double estimatedGhgReductionTonnes = MBTCompostingConstants.EMISSION_FACTOR.getValue() * organicWasteTreatedTonsPerYear;
        mitigation.setEstimatedGhgReductionTonnesPerYear(estimatedGhgReductionTonnes);
        
        Double estimatedGhgReductionKilotonnes = estimatedGhgReductionTonnes / 1000;
        mitigation.setEstimatedGhgReductionKilotonnesPerYear(estimatedGhgReductionKilotonnes);
        
        Double adjustedBauEmission = bauEmissionInKilotonnes - estimatedGhgReductionKilotonnes;
        mitigation.setAdjustedBauEmissionBiologicalTreatment(adjustedBauEmission);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<MBTCompostingMitigation> getAllMBTCompostingMitigation(Integer year) {
        Specification<MBTCompostingMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
