package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.LightBulbParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.LightBulbParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.model.LightBulbParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.repository.ILightBulbParameterRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.service.ILightBulbParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LightBulbParameterServiceImpl implements ILightBulbParameterService {

    private final ILightBulbParameterRepository repository;

    @Override
    @Transactional
    public LightBulbParameterResponseDto createLightBulbParameter(LightBulbParameterDto dto) {
        LightBulbParameter parameter = new LightBulbParameter();
        parameter.setEmissionFactor(dto.getEmissionFactor());
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);

        LightBulbParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public LightBulbParameterResponseDto updateLightBulbParameter(UUID id, LightBulbParameterDto dto) {
        LightBulbParameter parameter = repository.findById(id).orElseThrow(() -> new RuntimeException("Waste to WtE Parameter not found with id: " + id));

        parameter.setEmissionFactor(dto.getEmissionFactor());
        // Preserve isActive status on update

        LightBulbParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public LightBulbParameterResponseDto getLightBulbParameterById(UUID id) {
        LightBulbParameter parameter = repository.findById(id).orElseThrow(() -> new RuntimeException("Waste to WtE Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LightBulbParameterResponseDto> getAllLightBulbParameters() {
        // Sort: active first (true), then by createdAt DESC (the latest first)
        return repository.findAll().stream().sorted((a, b) -> {
            // The first sort by isActive: true (active) comes first
            if (a.getIsActive() != b.getIsActive()) {
                return b.getIsActive() ? 1 : -1; // true (active) comes first
            }
            // Then sort by createdAt DESC (latest first)
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        }).map(this::mapEntityToResponseDto).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void disable(UUID id) {
        LightBulbParameter parameter = repository.findById(id).orElseThrow(() -> new RuntimeException("Waste to WtE Parameter not found with id: " + id));

        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public LightBulbParameterResponseDto getLatestActive() {
        LightBulbParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc().orElseThrow(() -> new RuntimeException("No active LightBulbParameter found"));

        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps entity to response DTO
     */
    private LightBulbParameterResponseDto mapEntityToResponseDto(LightBulbParameter entity) {
        LightBulbParameterResponseDto dto = new LightBulbParameterResponseDto();
        dto.setId(entity.getId());
        dto.setEmissionFactor(entity.getEmissionFactor());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

