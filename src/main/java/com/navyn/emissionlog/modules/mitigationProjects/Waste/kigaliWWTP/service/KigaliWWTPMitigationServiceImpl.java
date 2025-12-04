package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants.KigaliWWTPConstants;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants.WWTPProjectPhase;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.repository.KigaliWWTPMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasProjectPhase;
import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class KigaliWWTPMitigationServiceImpl implements KigaliWWTPMitigationService {
    
    private final KigaliWWTPMitigationRepository repository;
    
    @Override
    public KigaliWWTPMitigation createKigaliWWTPMitigation(KigaliWWTPMitigationDto dto) {
        // Validate phase precedence
        validatePhasePrecedence(dto.getProjectPhase(), null);
        
        KigaliWWTPMitigation mitigation = new KigaliWWTPMitigation();
        
        // Convert phase capacity to standard unit (m³/day)
        double phaseCapacityInCubicMetersPerDay = dto.getPhaseCapacityUnit().toCubicMetersPerDay(dto.getPhaseCapacityPerDay());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setProjectPhase(dto.getProjectPhase());
        mitigation.setPhaseCapacityPerDay(phaseCapacityInCubicMetersPerDay);
        mitigation.setConnectedHouseholds(dto.getConnectedHouseholds());
        mitigation.setConnectedHouseholdsPercentage(dto.getConnectedHouseholdsPercentage());
        
        // Get constants
        double plantEfficiency = KigaliWWTPConstants.PLANT_OPERATIONAL_EFFICIENCY.getValue();
        double co2ePerM3 = KigaliWWTPConstants.CO2E_PER_M3_SLUDGE.getValue();
        
        // Calculations
        // 1. Effective Daily Flow (m³/day) = Plant Operational Efficiency × Phase capacity (m³/day) × Connected Households (%)
        double effectiveDailyFlow = plantEfficiency * phaseCapacityInCubicMetersPerDay * dto.getConnectedHouseholdsPercentage();
        mitigation.setEffectiveDailyFlow(effectiveDailyFlow);
        
        // 2. Annual Sludge Treated (m³/year) = Effective Daily Flow (m³/day) × 365
        double annualSludgeTreated = effectiveDailyFlow * 365;
        mitigation.setAnnualSludgeTreated(annualSludgeTreated);
        
        // 3. Annual Emissions Reduction (tCO₂e) = Annual Sludge Treated (m³) × CO₂e per m³ sludge (kg CO₂e per m³) / 1000
        double annualEmissionsReductionTonnes = (annualSludgeTreated * co2ePerM3) / 1000;
        mitigation.setAnnualEmissionsReductionTonnes(annualEmissionsReductionTonnes);
        
        // 4. Annual Emissions Reduction (ktCO₂e) = Annual Emissions Reduction (tCO₂e) / 1000
        double annualEmissionsReductionKilotonnes = annualEmissionsReductionTonnes / 1000;
        mitigation.setAnnualEmissionsReductionKilotonnes(annualEmissionsReductionKilotonnes);
        
        return repository.save(mitigation);
    }
    
    @Override
    public KigaliWWTPMitigation updateKigaliWWTPMitigation(UUID id, KigaliWWTPMitigationDto dto) {
        KigaliWWTPMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Kigali WWTP Mitigation record not found with id: " + id));
        
        // Validate phase precedence (exclude current record from validation)
        validatePhasePrecedence(dto.getProjectPhase(), id);
        
        // Convert phase capacity to standard unit (m³/day)
        double phaseCapacityInCubicMetersPerDay = dto.getPhaseCapacityUnit().toCubicMetersPerDay(dto.getPhaseCapacityPerDay());
        
        // Update user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setProjectPhase(dto.getProjectPhase());
        mitigation.setPhaseCapacityPerDay(phaseCapacityInCubicMetersPerDay);
        mitigation.setConnectedHouseholds(dto.getConnectedHouseholds());
        mitigation.setConnectedHouseholdsPercentage(dto.getConnectedHouseholdsPercentage());
        
        // Recalculate derived fields
        double plantEfficiency = KigaliWWTPConstants.PLANT_OPERATIONAL_EFFICIENCY.getValue();
        double co2ePerM3 = KigaliWWTPConstants.CO2E_PER_M3_SLUDGE.getValue();
        
        double effectiveDailyFlow = plantEfficiency * phaseCapacityInCubicMetersPerDay * dto.getConnectedHouseholdsPercentage();
        mitigation.setEffectiveDailyFlow(effectiveDailyFlow);
        
        double annualSludgeTreated = effectiveDailyFlow * 365;
        mitigation.setAnnualSludgeTreated(annualSludgeTreated);
        
        double annualEmissionsReductionTonnes = (annualSludgeTreated * co2ePerM3) / 1000;
        mitigation.setAnnualEmissionsReductionTonnes(annualEmissionsReductionTonnes);
        
        double annualEmissionsReductionKilotonnes = annualEmissionsReductionTonnes / 1000;
        mitigation.setAnnualEmissionsReductionKilotonnes(annualEmissionsReductionKilotonnes);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<KigaliWWTPMitigation> getAllKigaliWWTPMitigation(Integer year, WWTPProjectPhase projectPhase) {
        Specification<KigaliWWTPMitigation> spec = Specification
                .<KigaliWWTPMitigation>where(hasYear(year))
                .and(hasProjectPhase(projectPhase));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    /**
     * Validates that the new phase is not smaller than the maximum existing phase.
     * Phases must progress in ascending order: NONE < PHASE_I < PHASE_II < PHASE_III
     * 
     * @param newPhase The phase to validate
     * @param excludeId ID to exclude from validation (for updates)
     * @throws RuntimeException if phase precedence is violated
     */
    private void validatePhasePrecedence(WWTPProjectPhase newPhase, UUID excludeId) {
        // Get the maximum phase from existing records
        repository.findTopByOrderByProjectPhaseDesc()
            .ifPresent(maxRecord -> {
                // Exclude the current record being updated
                if (excludeId != null && maxRecord.getId().equals(excludeId)) {
                    return;
                }
                
                WWTPProjectPhase maxPhase = maxRecord.getProjectPhase();
                
                // Check if new phase is smaller than max phase
                if (newPhase.ordinal() < maxPhase.ordinal()) {
                    throw new RuntimeException(
                        String.format("Cannot set phase to %s. The project has already reached %s. " +
                                     "Phases cannot go backward.", 
                                     newPhase.getDisplayName(), maxPhase.getDisplayName())
                    );
                }
            });
    }
}
