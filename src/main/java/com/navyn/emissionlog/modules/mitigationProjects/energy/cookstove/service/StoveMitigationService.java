package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveInstallationDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigationYear;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveMitigationYearRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveTypeRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Transactional
    public StoveMitigationYear createMitigation(StoveInstallationDTO dto) {
        return calculateAndSave(new StoveMitigationYear(), dto);
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
        // Simply deleting the record is safe as it holds the foreign key.
        // No other tables depend on it.
        if (!mitigationRepository.existsById(id)) {
            throw new EntityNotFoundException("Mitigation record not found: " + id);
        }
        mitigationRepository.deleteById(id);
    }

    @Transactional
    public StoveMitigationYear updateById(UUID id, StoveInstallationDTO dto) {
        StoveMitigationYear existing = findById(id);
        return calculateAndSave(existing, dto);
    }

    private StoveMitigationYear calculateAndSave(StoveMitigationYear entity, StoveInstallationDTO dto) {
        UUID stoveTypeId = dto.getStoveTypeId();
        StoveType stoveType = stoveTypeRepository.findById(stoveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("StoveType not found: " + stoveTypeId));

        int year = dto.getYear();

        int previousUnits = mitigationRepository
                .findTopByStoveTypeIdAndYearLessThanOrderByYearDesc(stoveTypeId, year)
                .map(StoveMitigationYear::getUnitsInstalled)
                .orElse(0);

        int unitsInstalledThisYear = dto.getUnitsInstalledThisYear();
        int differenceInstalled = unitsInstalledThisYear - previousUnits;

        double constant = (stoveType.getBaselinePercentage() * 0.15) / 0.25;

        double conversionFactor = 0.029;
        double stoveFactor = 4 * 112 * 0.647;
        double assumptionsC11 = 8.43;
        double scale = 1e-3;

        double avoidedEmissions = (constant * differenceInstalled)
                * conversionFactor
                * (stoveFactor + assumptionsC11)
                * scale;

        entity.setStoveType(stoveType);
        entity.setYear(year);
        entity.setUnitsInstalled(unitsInstalledThisYear);
        entity.setBau(dto.getBau());
        entity.setDifferenceInstalled(differenceInstalled);
        entity.setConstantValue(constant);
        entity.setAvoidedEmissions(avoidedEmissions);

        StoveMitigationYear saved = mitigationRepository.save(entity);

        double totalAvoided = mitigationRepository.sumAvoidedEmissionsByYear(year);
        double adjustment = dto.getBau() - totalAvoided;

        saved.setTotalAvoidedEmissions(totalAvoided);
        saved.setAdjustment(adjustment);

        return mitigationRepository.save(saved);
    }
}
