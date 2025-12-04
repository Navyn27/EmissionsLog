package com.navyn.emissionlog.modules.mitigationProjects.Energy.waterheat.service;

import com.navyn.emissionlog.modules.mitigationProjects.Energy.waterheat.models.WaterHeatParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.waterheat.repository.WaterHeatParameterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WaterHeatParameterService  {

    private final WaterHeatParameterRepository repository;

    public WaterHeatParameterService(WaterHeatParameterRepository repository) {
        this.repository = repository;
    }

    public WaterHeatParameter create(WaterHeatParameter param) {
        return repository.save(param);
    }

    public List<WaterHeatParameter> getAll() {
        return repository.findAll();
    }

    public WaterHeatParameter getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WaterHeatParameter not found"));
    }

    public WaterHeatParameter update(UUID id, WaterHeatParameter newParam) {
        WaterHeatParameter existing = getById(id);

        existing.setAverageWaterHeat(newParam.getAverageWaterHeat());
        existing.setDeltaTemperature(newParam.getDeltaTemperature());
        existing.setSpecificHeatWater(newParam.getSpecificHeatWater());

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
