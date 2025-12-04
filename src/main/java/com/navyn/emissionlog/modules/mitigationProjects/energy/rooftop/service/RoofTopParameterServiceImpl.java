package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model.RoofTopParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.repository.IRoofTopParameterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoofTopParameterServiceImpl implements IRoofTopParameterService {

    private final IRoofTopParameterRepository repository;

    @Override
    @Transactional
    public RoofTopParameterResponseDto create(RoofTopParameterDto dto) {
        RoofTopParameter parameter = new RoofTopParameter();
        mapDtoToEntity(dto, parameter);

        // Calculate transient fields
        calculateTransientFields(parameter);

        RoofTopParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    public RoofTopParameterResponseDto getById(UUID id) {
        RoofTopParameter parameter = repository.findById(id).orElseThrow(() -> new RuntimeException("RoofTopParameter not found with id: " + id));

        // Calculate transient fields
        calculateTransientFields(parameter);

        return mapEntityToResponseDto(parameter);
    }

    @Override
    public List<RoofTopParameterResponseDto> getAll() {
        return repository.findAll().stream().map(parameter -> {
            calculateTransientFields(parameter);
            return mapEntityToResponseDto(parameter);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoofTopParameterResponseDto update(UUID id, RoofTopParameterDto dto) {
        RoofTopParameter parameter = repository.findById(id).orElseThrow(() -> new RuntimeException("RoofTopParameter not found with id: " + id));

        mapDtoToEntity(dto, parameter);

        // Calculate transient fields
        calculateTransientFields(parameter);

        RoofTopParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("RoofTopParameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public RoofTopParameterResponseDto getLatest() {
        List<RoofTopParameter> all = repository.findAll();
        if (all.isEmpty()) {
            throw new RuntimeException("No RoofTopParameter found");
        }

        // Get the latest by created date
        RoofTopParameter latest = all.stream().max((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt())).orElseThrow(() -> new RuntimeException("No RoofTopParameter found"));

        calculateTransientFields(latest);
        return mapEntityToResponseDto(latest);
    }

    private void mapDtoToEntity(RoofTopParameterDto dto, RoofTopParameter entity) {
        entity.setSolarPVCapacity(dto.getSolarPVCapacity());
        entity.setEnergyOutPut(dto.getEnergyOutPut());
        entity.setPercentageOutPutDisplacedDiesel(dto.getPercentageOutPutDisplacedDiesel());
        entity.setAvoidedDieselConsumption(dto.getAvoidedDieselConsumption());
        entity.setDieselEnergyContent(dto.getDieselEnergyContent());
        entity.setGensetEfficiency(dto.getGensetEfficiency());
        entity.setConstant(dto.getConstant());
    }

    private RoofTopParameterResponseDto mapEntityToResponseDto(RoofTopParameter entity) {
        RoofTopParameterResponseDto dto = new RoofTopParameterResponseDto();
        dto.setId(entity.getId());
        dto.setSolarPVCapacity(entity.getSolarPVCapacity());
        dto.setEnergyOutPut(entity.getEnergyOutPut());
        dto.setPercentageOutPutDisplacedDiesel(entity.getPercentageOutPutDisplacedDiesel());
        dto.setAvoidedDieselConsumption(entity.getAvoidedDieselConsumption());
        dto.setDieselEnergyContent(entity.getDieselEnergyContent());
        dto.setGensetEfficiency(entity.getGensetEfficiency());
        dto.setConstant(entity.getConstant());
        dto.setAvoidedDieselConsumptionCalculated(entity.getAvoidedDieselConsumptionCalculated());
        dto.setAvoidedDieselConsumptionAverage(entity.getAvoidedDieselConsumptionAverage());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    private void calculateTransientFields(RoofTopParameter parameter) {
        // Calculate avoidedDieselConsumptionCalculated
        // Formula: ((energyOutPut * constant (MJ/MWh)) / dieselEnergyContent) / gensetEfficiency(%) / 1_000_000 * percentageOutPutDisplacedDiesel
        double avoidedDieselConsumptionCalculated = (((parameter.getEnergyOutPut() * parameter.getConstant()) / parameter.getDieselEnergyContent()) / (parameter.getGensetEfficiency() / 100.0)  // Convert percentage to decimal
        ) / 1_000_000.0 * (parameter.getPercentageOutPutDisplacedDiesel() / 100);

        parameter.setAvoidedDieselConsumptionCalculated(avoidedDieselConsumptionCalculated);

        // Calculate avoidedDieselConsumptionAverage
        double avoidedDieselConsumptionAverage = (parameter.getAvoidedDieselConsumption() + avoidedDieselConsumptionCalculated) / 2.0;
        parameter.setAvoidedDieselConsumptionAverage(avoidedDieselConsumptionAverage);
    }
}
