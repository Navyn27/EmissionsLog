package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementParameter;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.repositories.SettlementTreesParameterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SettlementTreesParameterServiceImpl implements SettlementTreesParameterService {

    private final SettlementTreesParameterRepository repository;

    @Override
    @Transactional
    public SettlementTreesParameterResponseDto create(SettlementTreesParameterDto dto) {
        SettlementParameter parameter = new SettlementParameter();
        mapDtoToEntity(dto, parameter);
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        SettlementParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SettlementTreesParameterResponseDto getById(UUID id) {
        SettlementParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SettlementTreesParameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettlementTreesParameterResponseDto> getAll() {
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
    public SettlementTreesParameterResponseDto update(UUID id, SettlementTreesParameterDto dto) {
        SettlementParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SettlementTreesParameter not found with id: " + id));
        
        mapDtoToEntity(dto, parameter);
        // Preserve isActive status on update
        SettlementParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        SettlementParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SettlementTreesParameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public SettlementTreesParameterResponseDto getLatestActive() {
        SettlementParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active SettlementTreesParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps DTO fields to entity
     */
    private void mapDtoToEntity(SettlementTreesParameterDto dto, SettlementParameter entity) {
        entity.setConversationM3ToTonnes(dto.getConversationM3ToTonnes());
        entity.setRatioOfBelowGroundBiomass(dto.getRatioOfBelowGroundBiomass());
        entity.setCarbonContent(dto.getCarbonContent());
        entity.setCarbonToC02(dto.getCarbonToC02());
    }

    /**
     * Maps entity to response DTO
     */
    private SettlementTreesParameterResponseDto mapEntityToResponseDto(SettlementParameter entity) {
        SettlementTreesParameterResponseDto dto = new SettlementTreesParameterResponseDto();
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

