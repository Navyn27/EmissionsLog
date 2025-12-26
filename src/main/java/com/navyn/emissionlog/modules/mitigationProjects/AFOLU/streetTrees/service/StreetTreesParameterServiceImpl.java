package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.models.StreetTreesParameter;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.repositories.StreetTreesParameterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StreetTreesParameterServiceImpl implements StreetTreesParameterService {

    private final StreetTreesParameterRepository repository;

    @Override
    @Transactional
    public StreetTreesParameterResponseDto create(StreetTreesParameterDto dto) {
        StreetTreesParameter parameter = new StreetTreesParameter();
        mapDtoToEntity(dto, parameter);
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        StreetTreesParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StreetTreesParameterResponseDto getById(UUID id) {
        StreetTreesParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("StreetTreesParameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StreetTreesParameterResponseDto> getAll() {
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
    public StreetTreesParameterResponseDto update(UUID id, StreetTreesParameterDto dto) {
        StreetTreesParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("StreetTreesParameter not found with id: " + id));
        
        mapDtoToEntity(dto, parameter);
        // Preserve isActive status on update
        StreetTreesParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        StreetTreesParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("StreetTreesParameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public StreetTreesParameterResponseDto getLatestActive() {
        StreetTreesParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active StreetTreesParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps DTO fields to entity
     */
    private void mapDtoToEntity(StreetTreesParameterDto dto, StreetTreesParameter entity) {
        entity.setConversationM3ToTonnes(dto.getConversationM3ToTonnes());
        entity.setRatioOfBelowGroundBiomass(dto.getRatioOfBelowGroundBiomass());
        entity.setCarbonContent(dto.getCarbonContent());
        entity.setCarbonToC02(dto.getCarbonToC02());
    }

    /**
     * Maps entity to response DTO
     */
    private StreetTreesParameterResponseDto mapEntityToResponseDto(StreetTreesParameter entity) {
        StreetTreesParameterResponseDto dto = new StreetTreesParameterResponseDto();
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

