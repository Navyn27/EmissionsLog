package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.parameters;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.FireWoodParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.FireWoodParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.parameters.FireWoodParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.parameters.FireWoodParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FireWoodParameterServiceImpl implements IFireWoodParameterService {

    private final FireWoodParameterRepository repository;

    @Override
    @Transactional
    public FireWoodParameterResponseDto createParameter(FireWoodParameterDto dto) {
        FireWoodParameter parameter = new FireWoodParameter();
        parameter.setNetCalorificValue(dto.getNetCalorificValue());
        parameter.setEmissionFactor(dto.getEmissionFactor());
        parameter.setAdjustedEmissionFactor(dto.getAdjustedEmissionFactor());
        parameter.setBiomass(dto.getBiomass());
        parameter.setFuelConsumption(dto.getFuelConsumption());
        parameter.setEfficiency(dto.getEfficiency());
        parameter.setSize(dto.getSize());
        parameter.setIsActive(true);

        FireWoodParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public FireWoodParameterResponseDto updateParameter(UUID id, FireWoodParameterDto dto) {
        FireWoodParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("FireWood Parameter not found with id: " + id));

        parameter.setNetCalorificValue(dto.getNetCalorificValue());
        parameter.setEmissionFactor(dto.getEmissionFactor());
        parameter.setAdjustedEmissionFactor(dto.getAdjustedEmissionFactor());
        parameter.setBiomass(dto.getBiomass());
        parameter.setFuelConsumption(dto.getFuelConsumption());
        parameter.setEfficiency(dto.getEfficiency());
        parameter.setSize(dto.getSize());

        FireWoodParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public FireWoodParameterResponseDto getParameterById(UUID id) {
        FireWoodParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("FireWood Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FireWoodParameterResponseDto> getAllParameters() {
        return repository.findAllByOrderByIsActiveDescCreatedAtDesc().stream()
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FireWoodParameterResponseDto getLatestActive() {
        FireWoodParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active FireWood Parameter found"));
        return mapEntityToResponseDto(latestActive);
    }

    @Override
    @Transactional
    public void disableParameter(UUID id) {
        FireWoodParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("FireWood Parameter not found with id: " + id));
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional
    public void deleteParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("FireWood Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private FireWoodParameterResponseDto mapEntityToResponseDto(FireWoodParameter entity) {
        FireWoodParameterResponseDto dto = new FireWoodParameterResponseDto();
        dto.setId(entity.getId());
        dto.setNetCalorificValue(entity.getNetCalorificValue());
        dto.setEmissionFactor(entity.getEmissionFactor());
        dto.setAdjustedEmissionFactor(entity.getAdjustedEmissionFactor());
        dto.setBiomass(entity.getBiomass());
        dto.setFuelConsumption(entity.getFuelConsumption());
        dto.setEfficiency(entity.getEfficiency());
        dto.setSize(entity.getSize());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

