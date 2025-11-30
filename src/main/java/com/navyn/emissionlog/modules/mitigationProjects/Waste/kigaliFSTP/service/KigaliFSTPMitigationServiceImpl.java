package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.constants.KigaliFSTPConstants;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models.KigaliFSTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.repository.KigaliFSTPMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class KigaliFSTPMitigationServiceImpl implements KigaliFSTPMitigationService {
    
    private final KigaliFSTPMitigationRepository repository;
    
    @Override
    public KigaliFSTPMitigation createKigaliFSTPMitigation(KigaliFSTPMitigationDto dto) {
        KigaliFSTPMitigation mitigation = new KigaliFSTPMitigation();
        
        // Convert phase capacity to standard unit (m³/day)
        double phaseCapacityInCubicMetersPerDay = dto.getPhaseCapacityUnit().toCubicMetersPerDay(dto.getPhaseCapacityPerDay());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setProjectPhase(dto.getProjectPhase());
        mitigation.setPhaseCapacityPerDay(phaseCapacityInCubicMetersPerDay);
        mitigation.setPlantOperationalEfficiency(dto.getPlantOperationalEfficiency());
        
        // Calculations
        // 1. Effective Daily Treatment (m³/day) = Plant Operational Efficiency × Phase capacity (m³/day)
        Double plantEfficiency = dto.getPlantOperationalEfficiency();
        Double phaseCapacity = phaseCapacityInCubicMetersPerDay;
        Double effectiveDailyTreatment = plantEfficiency * phaseCapacity;
        mitigation.setEffectiveDailyTreatment(effectiveDailyTreatment);
        
        // 2. Annual Sludge Treated (m³) = Effective Daily Treatment (m³/day) × 365
        Double annualSludgeTreated = effectiveDailyTreatment * 365;
        mitigation.setAnnualSludgeTreated(annualSludgeTreated);
        
        // 3. Annual Emissions Reduction (tCO₂e) = Annual Sludge Treated (m³) × CO₂e per m³ sludge (kg CO₂e per m³) / 1000
        Double co2ePerM3 = KigaliFSTPConstants.CO2E_PER_M3_SLUDGE.getValue();
        Double annualEmissionsReductionTonnes = (annualSludgeTreated * co2ePerM3) / 1000;
        mitigation.setAnnualEmissionsReductionTonnes(annualEmissionsReductionTonnes);
        
        // 4. Annual Emissions Reduction (ktCO₂e) = Annual Emissions Reduction (tCO₂e) / 1000
        Double annualEmissionsReductionKilotonnes = annualEmissionsReductionTonnes / 1000;
        mitigation.setAnnualEmissionsReductionKilotonnes(annualEmissionsReductionKilotonnes);
        
        return repository.save(mitigation);
    }
    
    @Override
    public KigaliFSTPMitigation updateKigaliFSTPMitigation(Long id, KigaliFSTPMitigationDto dto) {
        KigaliFSTPMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Kigali FSTP Mitigation record not found with id: " + id));
        
        // Convert phase capacity to standard unit (m³/day)
        double phaseCapacityInCubicMetersPerDay = dto.getPhaseCapacityUnit().toCubicMetersPerDay(dto.getPhaseCapacityPerDay());
        
        // Update user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setProjectPhase(dto.getProjectPhase());
        mitigation.setPhaseCapacityPerDay(phaseCapacityInCubicMetersPerDay);
        mitigation.setPlantOperationalEfficiency(dto.getPlantOperationalEfficiency());
        
        // Recalculate derived fields
        Double plantEfficiency = dto.getPlantOperationalEfficiency();
        Double phaseCapacity = phaseCapacityInCubicMetersPerDay;
        Double effectiveDailyTreatment = plantEfficiency * phaseCapacity;
        mitigation.setEffectiveDailyTreatment(effectiveDailyTreatment);
        
        Double annualSludgeTreated = effectiveDailyTreatment * 365;
        mitigation.setAnnualSludgeTreated(annualSludgeTreated);
        
        Double co2ePerM3 = KigaliFSTPConstants.CO2E_PER_M3_SLUDGE.getValue();
        Double annualEmissionsReductionTonnes = (annualSludgeTreated * co2ePerM3) / 1000;
        mitigation.setAnnualEmissionsReductionTonnes(annualEmissionsReductionTonnes);
        
        Double annualEmissionsReductionKilotonnes = annualEmissionsReductionTonnes / 1000;
        mitigation.setAnnualEmissionsReductionKilotonnes(annualEmissionsReductionKilotonnes);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<KigaliFSTPMitigation> getAllKigaliFSTPMitigation(Integer year) {
        Specification<KigaliFSTPMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
