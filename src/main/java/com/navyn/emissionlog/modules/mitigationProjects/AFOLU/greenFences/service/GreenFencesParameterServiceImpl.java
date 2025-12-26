package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.models.GreenFencesParameters;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.repositories.GreenFencesParameterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GreenFencesParameterServiceImpl implements GreenFencesParameterService {

    private final GreenFencesParameterRepository repository;

    @Override
    @Transactional
    public GreenFencesParameterResponseDto create(GreenFencesParameterDto dto) {
        GreenFencesParameters parameter = new GreenFencesParameters();
        mapDtoToEntity(dto, parameter);
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        GreenFencesParameters saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public GreenFencesParameterResponseDto getById(UUID id) {
        GreenFencesParameters parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("GreenFencesParameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GreenFencesParameterResponseDto> getAll() {
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
    public GreenFencesParameterResponseDto update(UUID id, GreenFencesParameterDto dto) {
        GreenFencesParameters parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("GreenFencesParameter not found with id: " + id));
        
        mapDtoToEntity(dto, parameter);
        // Preserve isActive status on update
        GreenFencesParameters updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        GreenFencesParameters parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("GreenFencesParameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public GreenFencesParameterResponseDto getLatestActive() {
        GreenFencesParameters latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active GreenFencesParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps DTO fields to entity
     */
    private void mapDtoToEntity(GreenFencesParameterDto dto, GreenFencesParameters entity) {
        entity.setCarbonContent(dto.getCarbonContent());
        entity.setRatioOfBelowGroundBiomass(dto.getRatioOfBelowGroundBiomass());
        entity.setCarbonContentInDryWoods(dto.getCarbonContentInDryWoods());
        entity.setCarbonToC02(dto.getCarbonToC02());
    }

    /**
     * Maps entity to response DTO
     */
    private GreenFencesParameterResponseDto mapEntityToResponseDto(GreenFencesParameters entity) {
        GreenFencesParameterResponseDto dto = new GreenFencesParameterResponseDto();
        dto.setId(entity.getId());
        dto.setCarbonContent(entity.getCarbonContent());
        dto.setRatioOfBelowGroundBiomass(entity.getRatioOfBelowGroundBiomass());
        dto.setCarbonContentInDryWoods(entity.getCarbonContentInDryWoods());
        dto.setCarbonToC02(entity.getCarbonToC02());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

