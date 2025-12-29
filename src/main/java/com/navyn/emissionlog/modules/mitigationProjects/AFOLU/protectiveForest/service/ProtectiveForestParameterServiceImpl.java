package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models.ProtectiveForestParameter;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.repositories.ProtectiveForestParameterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProtectiveForestParameterServiceImpl implements ProtectiveForestParameterService {

    private final ProtectiveForestParameterRepository repository;

    @Override
    @Transactional
    public ProtectiveForestParameterResponseDto create(ProtectiveForestParameterDto dto) {
        ProtectiveForestParameter parameter = new ProtectiveForestParameter();
        mapDtoToEntity(dto, parameter);
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);

        ProtectiveForestParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProtectiveForestParameterResponseDto getById(UUID id) {
        ProtectiveForestParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProtectiveForestParameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProtectiveForestParameterResponseDto> getAll() {
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
    public ProtectiveForestParameterResponseDto update(UUID id, ProtectiveForestParameterDto dto) {
        ProtectiveForestParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProtectiveForestParameter not found with id: " + id));

        mapDtoToEntity(dto, parameter);
        // Preserve isActive status on update
        ProtectiveForestParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        ProtectiveForestParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProtectiveForestParameter not found with id: " + id));

        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public ProtectiveForestParameterResponseDto getLatestActive() {
        ProtectiveForestParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active ProtectiveForestParameter found"));

        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps DTO fields to entity
     */
    private void mapDtoToEntity(ProtectiveForestParameterDto dto, ProtectiveForestParameter entity) {
        entity.setConversationM3ToTonnes(dto.getConversationM3ToTonnes());
        entity.setRatioOfBelowGroundBiomass(dto.getRatioOfBelowGroundBiomass());
        entity.setCarbonContent(dto.getCarbonContent());
        entity.setCarbonToC02(dto.getCarbonToC02());
    }

    /**
     * Maps entity to response DTO
     */
    private ProtectiveForestParameterResponseDto mapEntityToResponseDto(ProtectiveForestParameter entity) {
        ProtectiveForestParameterResponseDto dto = new ProtectiveForestParameterResponseDto();
        dto.setId(entity.getId());
        dto.setConversationM3ToTonnes(entity.getConversationM3ToTonnes());
        dto.setRatioOfBelowGroundBiomass(entity.getRatioOfBelowGroundBiomass());
        dto.setCarbonContent(entity.getCarbonContent());
        dto.setCarbonToC02(entity.getCarbonToC02());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
