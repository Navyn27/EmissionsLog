package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models.CropRotationParameters;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.repositories.CropRotationParameterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CropRotationParameterServiceImpl implements CropRotationParameterService {

    private final CropRotationParameterRepository repository;

    @Override
    @Transactional
    public CropRotationParameterResponseDto create(CropRotationParameterDto dto) {
        CropRotationParameters parameter = new CropRotationParameters();
        mapDtoToEntity(dto, parameter);
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        CropRotationParameters saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CropRotationParameterResponseDto getById(UUID id) {
        CropRotationParameters parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("CropRotationParameters not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropRotationParameterResponseDto> getAll() {
        // Sort: active first (true), then by createdAt DESC (the latest first)
        return repository.findAll().stream()
                .sorted((a, b) -> {
                    // The first sort by isActive: true (active) comes first
                    if (a.getIsActive() != b.getIsActive()) {
                        return b.getIsActive() ? 1 : -1; // true (active) comes first
                    }
                    // Then sort by createdAt DESC (latest first)
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CropRotationParameterResponseDto update(UUID id, CropRotationParameterDto dto) {
        CropRotationParameters parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("CropRotationParameters not found with id: " + id));
        
        mapDtoToEntity(dto, parameter);
        // Preserve isActive status on update
        CropRotationParameters updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        CropRotationParameters parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("CropRotationParameters not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public CropRotationParameterResponseDto getLatestActive() {
        CropRotationParameters latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active CropRotationParameters found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps DTO fields to entity
     */
    private void mapDtoToEntity(CropRotationParameterDto dto, CropRotationParameters entity) {
        entity.setAboveGroundBiomass(dto.getAboveGroundBiomass());
        entity.setRatioOfBelowGroundBiomass(dto.getRatioOfBelowGroundBiomass());
        entity.setCarbonContent(dto.getCarbonContent());
        entity.setCarbonToC02(dto.getCarbonToC02());
    }

    /**
     * Maps entity to response DTO
     */
    private CropRotationParameterResponseDto mapEntityToResponseDto(CropRotationParameters entity) {
        CropRotationParameterResponseDto dto = new CropRotationParameterResponseDto();
        dto.setId(entity.getId());
        dto.setAboveGroundBiomass(entity.getAboveGroundBiomass());
        dto.setRatioOfBelowGroundBiomass(entity.getRatioOfBelowGroundBiomass());
        dto.setCarbonContent(entity.getCarbonContent());
        dto.setCarbonToC02(entity.getCarbonToC02());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

