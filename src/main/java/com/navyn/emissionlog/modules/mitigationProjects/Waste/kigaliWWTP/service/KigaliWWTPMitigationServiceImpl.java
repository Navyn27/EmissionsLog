package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants.KigaliWWTPConstants;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.repository.KigaliWWTPMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class KigaliWWTPMitigationServiceImpl implements KigaliWWTPMitigationService {
    
    private final KigaliWWTPMitigationRepository repository;
    
    @Override
    public KigaliWWTPMitigation createKigaliWWTPMitigation(KigaliWWTPMitigationDto dto) {
        KigaliWWTPMitigation mitigation = new KigaliWWTPMitigation();
        
        // Convert phase capacity to standard unit (m³/day)
        double phaseCapacityInCubicMetersPerDay = dto.getPhaseCapacityUnit().toCubicMetersPerDay(dto.getPhaseCapacityPerDay());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setPhaseCapacityPerDay(phaseCapacityInCubicMetersPerDay);
        mitigation.setConnectedHouseholds(dto.getConnectedHouseholds());
        mitigation.setConnectedHouseholdsPercentage(dto.getConnectedHouseholdsPercentage());
        
        // Get constants
        Double plantEfficiency = KigaliWWTPConstants.PLANT_OPERATIONAL_EFFICIENCY.getValue();
        Double co2ePerM3 = KigaliWWTPConstants.CO2E_PER_M3_SLUDGE.getValue();
        
        // Get user input values
        Double phaseCapacity = phaseCapacityInCubicMetersPerDay;
        Double connectedHouseholdsPercentage = dto.getConnectedHouseholdsPercentage();
        
        // Calculations
        // 1. Effective Daily Flow (m³/day) = Plant Operational Efficiency × Connected Households (%) × Phase capacity (m³/day)
        Double effectiveDailyFlow = plantEfficiency * connectedHouseholdsPercentage * phaseCapacity;
        mitigation.setEffectiveDailyFlow(effectiveDailyFlow);
        
        // 2. Annual Sludge Treated (m³) = Effective Daily Flow (m³/day) × 365
        Double annualSludgeTreated = effectiveDailyFlow * 365;
        mitigation.setAnnualSludgeTreated(annualSludgeTreated);
        
        // 3. Annual Emissions Reduction (tCO₂e) = Annual Sludge Treated (m³) × CO₂e per m³ sludge (kg CO₂e per m³) / 1000
        Double annualEmissionsReductionTonnes = (annualSludgeTreated * co2ePerM3) / 1000;
        mitigation.setAnnualEmissionsReductionTonnes(annualEmissionsReductionTonnes);
        
        // 4. Annual Emissions Reduction (ktCO₂e) = Annual Emissions Reduction (tCO₂e) / 1000
        Double annualEmissionsReductionKilotonnes = annualEmissionsReductionTonnes / 1000;
        mitigation.setAnnualEmissionsReductionKilotonnes(annualEmissionsReductionKilotonnes);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<KigaliWWTPMitigation> getAllKigaliWWTPMitigation(Integer year) {
        Specification<KigaliWWTPMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
