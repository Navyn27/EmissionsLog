package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.CreateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.UpdateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.model.LightBulb;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.repository.ILightBulbRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LightBulbServiceImpl implements ILightBulbService {
    private final ILightBulbRepository lightBulbRepository;

    @Override
    public LightBulb create(CreateLightBulbDTO lightBulbDTO) {
        LightBulb lightBulb = new LightBulb();
        lightBulb.setYear(lightBulbDTO.getYear());
        lightBulb.setTotalInstalledBulbsPerYear(lightBulbDTO.getTotalInstalledBulbsPerYear());
        lightBulb.setReductionCapacityPerBulb(lightBulbDTO.getReductionCapacityPerBulb());
        lightBulb.setEmissionFactor(lightBulbDTO.getEmissionFactor());
        lightBulb.setBau(lightBulbDTO.getBau());
        calculateAndSetFields(lightBulb);
        return lightBulbRepository.save(lightBulb);
    }

    @Override
    public List<LightBulb> getAll() {
        return lightBulbRepository.findAll();
    }

    @Override
    public LightBulb getById(UUID id) {
        return lightBulbRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("LightBulb not found"));
    }

    @Override
    public LightBulb update(UUID id, UpdateLightBulbDTO lightBulbDTO) {
        LightBulb lightBulb = getById(id);
        if (lightBulbDTO.getYear() != null) {
            lightBulb.setYear(lightBulbDTO.getYear());
        }
        if (lightBulbDTO.getTotalInstalledBulbsPerYear() != null) {
            lightBulb.setTotalInstalledBulbsPerYear(lightBulbDTO.getTotalInstalledBulbsPerYear());
        }
        if (lightBulbDTO.getReductionCapacityPerBulb() != null) {
            lightBulb.setReductionCapacityPerBulb(lightBulbDTO.getReductionCapacityPerBulb());
        }
        if (lightBulbDTO.getEmissionFactor() != null) {
            lightBulb.setEmissionFactor(lightBulbDTO.getEmissionFactor());
        }
        if (lightBulbDTO.getBau() != null) {
            lightBulb.setBau(lightBulbDTO.getBau());
        }
        calculateAndSetFields(lightBulb);
        return lightBulbRepository.save(lightBulb);
    }

    @Override
    public void delete(UUID id) {
        lightBulbRepository.deleteById(id);
    }

    private void calculateAndSetFields(LightBulb lightBulb) {
        double totalReductionPerYear = lightBulb.getReductionCapacityPerBulb() * lightBulb.getTotalInstalledBulbsPerYear();
        double netGhGMitigationAchieved = (totalReductionPerYear * lightBulb.getEmissionFactor()) / 1000;
        double scenarioGhGMitigationAchieved = lightBulb.getBau() - netGhGMitigationAchieved;

        lightBulb.setTotalReductionPerYear(totalReductionPerYear);
        lightBulb.setNetGhGMitigationAchieved(netGhGMitigationAchieved);
        lightBulb.setScenarioGhGMitigationAchieved(scenarioGhGMitigationAchieved);
    }
}
