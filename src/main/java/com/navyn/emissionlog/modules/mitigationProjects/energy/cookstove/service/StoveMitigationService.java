package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveInstallationDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigationYear;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveMitigationYearRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class StoveMitigationService {

    private final StoveMitigationYearRepository mitigationRepository;
    private final StoveTypeRepository stoveTypeRepository;

    public StoveMitigationService(StoveMitigationYearRepository mitigationRepository,
                                  StoveTypeRepository stoveTypeRepository) {
        this.mitigationRepository = mitigationRepository;
        this.stoveTypeRepository = stoveTypeRepository;
    }

    /**
     * Create a mitigation record based on user input and apply the required formulas.
     *
     * constant = (baselinePercentage * 0.15) / 0.25
     * avoidedEmission = (constant * differenceInstalled) * 0.029 * ((4*112*0.647) + 8.43) * 10^-3
     * totalAvoidedEmission = sum of avoidedEmission for all types in that year
     * adjustment = bau - totalAvoidedEmission
     */
    @Transactional
    public StoveMitigationYear createMitigation(StoveInstallationDTO dto) {
        UUID stoveTypeId = dto.getStoveTypeId();
        StoveType stoveType = stoveTypeRepository.findById(stoveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("StoveType not found: " + stoveTypeId));

        int year = dto.getYear();

        // Determine differenceInstalled compared to previous year for this stove type
        int previousUnits = mitigationRepository
                .findTopByStoveTypeIdAndYearLessThanOrderByYearDesc(stoveTypeId, year)
                .map(StoveMitigationYear::getUnitsInstalled)
                .orElse(0);

        int unitsInstalledThisYear = dto.getUnitsInstalledThisYear();
        int differenceInstalled = unitsInstalledThisYear - previousUnits;

        // constant = (baselinePercentage * 0.15) / 0.25
        double constant = (stoveType.getBaselinePercentage() * 0.15) / 0.25;

        // avoidedEmission = (constant * differenceInstalled) * 0.029 * ((4*112*0.647) + 8.43) * 10^-3
        double conversionFactor = 0.029;
        double stoveFactor = 4 * 112 * 0.647;
        double assumptionsC11 = 8.43;
        double scale = 1e-3;

        double avoidedEmissions = (constant * differenceInstalled)
                * conversionFactor
                * (stoveFactor + assumptionsC11)
                * scale;

        StoveMitigationYear entity = new StoveMitigationYear();
        entity.setStoveType(stoveType);
        entity.setYear(year);
        entity.setUnitsInstalled(unitsInstalledThisYear);
        entity.setBau(dto.getBau());
        entity.setDifferenceInstalled(differenceInstalled);
        entity.setConstantValue(constant);
        entity.setAvoidedEmissions(avoidedEmissions);

        // Persist first to include this record in the yearly sum
        StoveMitigationYear saved = mitigationRepository.save(entity);

        // totalAvoidedEmission = sum of avoidedEmission of each type for this year
        double totalAvoided = mitigationRepository.sumAvoidedEmissionsByYear(year);
        double adjustment = dto.getBau() - totalAvoided;

        saved.setTotalAvoidedEmissions(totalAvoided);
        saved.setAdjustment(adjustment);

        return mitigationRepository.save(saved);
    }

    public List<StoveMitigationYear> findAll() {
        return mitigationRepository.findAll();
    }

    public List<StoveMitigationYear> findByStoveType(UUID stoveTypeId) {
        return mitigationRepository.findByStoveTypeId(stoveTypeId);
    }

    public List<StoveMitigationYear> findByYear(int year) {
        return mitigationRepository.findByYear(year);
    }

    public StoveMitigationYear findById(UUID id) {
        return mitigationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mitigation record not found: " + id));
    }

    @Transactional
    public void deleteById(UUID id) {
        mitigationRepository.deleteById(id);
    }

    @Transactional
    public StoveMitigationYear updateById(UUID id, StoveInstallationDTO dto) {
        // Fetch existing record
        StoveMitigationYear existing = mitigationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mitigation record not found: " + id));

        // Validate and fetch StoveType
        UUID stoveTypeId = dto.getStoveTypeId();
        StoveType stoveType = stoveTypeRepository.findById(stoveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("StoveType not found: " + stoveTypeId));

        int year = dto.getYear();

        // Determine differenceInstalled compared to previous year for this stove type
        int previousUnits = mitigationRepository
                .findTopByStoveTypeIdAndYearLessThanOrderByYearDesc(stoveTypeId, year)
                .map(StoveMitigationYear::getUnitsInstalled)
                .orElse(0);

        int unitsInstalledThisYear = dto.getUnitsInstalledThisYear();
        int differenceInstalled = unitsInstalledThisYear - previousUnits;

        // constant = (baselinePercentage * 0.15) / 0.25
        double constant = (stoveType.getBaselinePercentage() * 0.15) / 0.25;

        // avoidedEmission = (constant * differenceInstalled) * 0.029 * ((4*112*0.647) + 8.43) * 10^-3
        double conversionFactor = 0.029;
        double stoveFactor = 4 * 112 * 0.647;
        double assumptionsC11 = 8.43;
        double scale = 1e-3;

        double avoidedEmissions = (constant * differenceInstalled)
                * conversionFactor
                * (stoveFactor + assumptionsC11)
                * scale;

        // Update entity fields
        existing.setStoveType(stoveType);
        existing.setYear(year);
        existing.setUnitsInstalled(unitsInstalledThisYear);
        existing.setBau(dto.getBau());
        existing.setDifferenceInstalled(differenceInstalled);
        existing.setConstantValue(constant);
        existing.setAvoidedEmissions(avoidedEmissions);

        // Persist first to include this record in the yearly sum
        StoveMitigationYear saved = mitigationRepository.save(existing);

        // totalAvoidedEmission = sum of avoidedEmission of each type for this year
        double totalAvoided = mitigationRepository.sumAvoidedEmissionsByYear(year);
        double adjustment = dto.getBau() - totalAvoided;

        saved.setTotalAvoidedEmissions(totalAvoided);
        saved.setAdjustment(adjustment);

        return mitigationRepository.save(saved);
    }
}
