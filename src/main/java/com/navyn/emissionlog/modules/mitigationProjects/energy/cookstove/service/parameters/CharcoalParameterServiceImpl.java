package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.parameters;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.CharcoalParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.CharcoalParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.parameters.CharcoalParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.parameters.CharcoalParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharcoalParameterServiceImpl implements ICharcoalParameterService {

    private final CharcoalParameterRepository repository;

    @Override
    @Transactional
    public CharcoalParameterResponseDto createParameter(CharcoalParameterDto dto) {
        CharcoalParameter parameter = new CharcoalParameter();
        parameter.setNetCalorificValue(dto.getNetCalorificValue());
        parameter.setEmissionFactor(dto.getEmissionFactor());
        parameter.setAdjustedEmissionFactor(dto.getAdjustedEmissionFactor());
        parameter.setBiomass(dto.getBiomass());
        parameter.setFuelConsumption(dto.getFuelConsumption());
        parameter.setEfficiency(dto.getEfficiency());
        parameter.setSize(dto.getSize());
        parameter.setIsActive(true);

        CharcoalParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public CharcoalParameterResponseDto updateParameter(UUID id, CharcoalParameterDto dto) {
        CharcoalParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charcoal Parameter not found with id: " + id));

        parameter.setNetCalorificValue(dto.getNetCalorificValue());
        parameter.setEmissionFactor(dto.getEmissionFactor());
        parameter.setAdjustedEmissionFactor(dto.getAdjustedEmissionFactor());
        parameter.setBiomass(dto.getBiomass());
        parameter.setFuelConsumption(dto.getFuelConsumption());
        parameter.setEfficiency(dto.getEfficiency());
        parameter.setSize(dto.getSize());

        CharcoalParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CharcoalParameterResponseDto getParameterById(UUID id) {
        CharcoalParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charcoal Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharcoalParameterResponseDto> getAllParameters() {
        return repository.findAllByOrderByIsActiveDescCreatedAtDesc().stream()
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CharcoalParameterResponseDto getLatestActive() {
        CharcoalParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active Charcoal Parameter found"));
        return mapEntityToResponseDto(latestActive);
    }

    @Override
    @Transactional
    public void disableParameter(UUID id) {
        CharcoalParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charcoal Parameter not found with id: " + id));
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional
    public void deleteParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Charcoal Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private CharcoalParameterResponseDto mapEntityToResponseDto(CharcoalParameter entity) {
        CharcoalParameterResponseDto dto = new CharcoalParameterResponseDto();
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

