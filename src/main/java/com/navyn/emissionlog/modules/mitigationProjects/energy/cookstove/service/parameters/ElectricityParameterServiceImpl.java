package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.parameters;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.ElectricityParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.ElectricityParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.parameters.ElectricityParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.parameters.ElectricityParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectricityParameterServiceImpl implements IElectricityParameterService {

    private final ElectricityParameterRepository repository;

    @Override
    @Transactional
    public ElectricityParameterResponseDto createParameter(ElectricityParameterDto dto) {
        ElectricityParameter parameter = new ElectricityParameter();
        parameter.setFuelConsumption(dto.getFuelConsumption());
        parameter.setEmissionFactor(dto.getEmissionFactor());
        parameter.setEfficiency(dto.getEfficiency());
        parameter.setSize(dto.getSize());
        parameter.setIsActive(true);

        ElectricityParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public ElectricityParameterResponseDto updateParameter(UUID id, ElectricityParameterDto dto) {
        ElectricityParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Electricity Parameter not found with id: " + id));

        parameter.setFuelConsumption(dto.getFuelConsumption());
        parameter.setEmissionFactor(dto.getEmissionFactor());
        parameter.setEfficiency(dto.getEfficiency());
        parameter.setSize(dto.getSize());

        ElectricityParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ElectricityParameterResponseDto getParameterById(UUID id) {
        ElectricityParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Electricity Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElectricityParameterResponseDto> getAllParameters() {
        return repository.findAllByOrderByIsActiveDescCreatedAtDesc().stream()
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ElectricityParameterResponseDto getLatestActive() {
        ElectricityParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active Electricity Parameter found"));
        return mapEntityToResponseDto(latestActive);
    }

    @Override
    @Transactional
    public void disableParameter(UUID id) {
        ElectricityParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Electricity Parameter not found with id: " + id));
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional
    public void deleteParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Electricity Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private ElectricityParameterResponseDto mapEntityToResponseDto(ElectricityParameter entity) {
        ElectricityParameterResponseDto dto = new ElectricityParameterResponseDto();
        dto.setId(entity.getId());
        dto.setFuelConsumption(entity.getFuelConsumption());
        dto.setEmissionFactor(entity.getEmissionFactor());
        dto.setEfficiency(entity.getEfficiency());
        dto.setSize(entity.getSize());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

