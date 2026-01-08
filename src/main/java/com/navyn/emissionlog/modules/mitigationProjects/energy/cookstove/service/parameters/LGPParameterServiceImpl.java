package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.parameters;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.LGPParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.LGPParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.parameters.LGPParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.parameters.LGPParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LGPParameterServiceImpl implements ILGPParameterService {

    private final LGPParameterRepository repository;

    @Override
    @Transactional
    public LGPParameterResponseDto createParameter(LGPParameterDto dto) {
        LGPParameter parameter = new LGPParameter();
        parameter.setNetCalorificValue(dto.getNetCalorificValue());
        parameter.setEmissionFactor(dto.getEmissionFactor());
        parameter.setAdjustedEmissionFactor(dto.getAdjustedEmissionFactor());
        parameter.setFuelConsumption(dto.getFuelConsumption());
        parameter.setEfficiency(dto.getEfficiency());
        parameter.setSize(dto.getSize());
        parameter.setIsActive(true);

        LGPParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public LGPParameterResponseDto updateParameter(UUID id, LGPParameterDto dto) {
        LGPParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("LGP Parameter not found with id: " + id));

        parameter.setNetCalorificValue(dto.getNetCalorificValue());
        parameter.setEmissionFactor(dto.getEmissionFactor());
        parameter.setAdjustedEmissionFactor(dto.getAdjustedEmissionFactor());
        parameter.setFuelConsumption(dto.getFuelConsumption());
        parameter.setEfficiency(dto.getEfficiency());
        parameter.setSize(dto.getSize());

        LGPParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public LGPParameterResponseDto getParameterById(UUID id) {
        LGPParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("LGP Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LGPParameterResponseDto> getAllParameters() {
        return repository.findAllByOrderByIsActiveDescCreatedAtDesc().stream()
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LGPParameterResponseDto getLatestActive() {
        LGPParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active LGP Parameter found"));
        return mapEntityToResponseDto(latestActive);
    }

    @Override
    @Transactional
    public void disableParameter(UUID id) {
        LGPParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("LGP Parameter not found with id: " + id));
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional
    public void deleteParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("LGP Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private LGPParameterResponseDto mapEntityToResponseDto(LGPParameter entity) {
        LGPParameterResponseDto dto = new LGPParameterResponseDto();
        dto.setId(entity.getId());
        dto.setNetCalorificValue(entity.getNetCalorificValue());
        dto.setEmissionFactor(entity.getEmissionFactor());
        dto.setAdjustedEmissionFactor(entity.getAdjustedEmissionFactor());
        dto.setFuelConsumption(entity.getFuelConsumption());
        dto.setEfficiency(entity.getEfficiency());
        dto.setSize(entity.getSize());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

