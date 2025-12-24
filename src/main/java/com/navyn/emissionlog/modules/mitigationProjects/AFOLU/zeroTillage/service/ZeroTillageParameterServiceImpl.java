package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageParameter;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.repositories.ZeroTillageParameterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ZeroTillageParameterServiceImpl implements ZeroTillageParameterService {

    private final ZeroTillageParameterRepository repository;

    @Override
    @Transactional
    public ZeroTillageParameterResponseDto create(ZeroTillageParameterDto dto) {
        ZeroTillageParameter parameter = new ZeroTillageParameter();
        mapDtoToEntity(dto, parameter);
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        ZeroTillageParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ZeroTillageParameterResponseDto getById(UUID id) {
        ZeroTillageParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ZeroTillageParameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZeroTillageParameterResponseDto> getAll() {
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
    public ZeroTillageParameterResponseDto update(UUID id, ZeroTillageParameterDto dto) {
        ZeroTillageParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ZeroTillageParameter not found with id: " + id));
        
        mapDtoToEntity(dto, parameter);
        // Preserve isActive status on update
        ZeroTillageParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        ZeroTillageParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ZeroTillageParameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public ZeroTillageParameterResponseDto getLatestActive() {
        ZeroTillageParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active ZeroTillageParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps DTO fields to entity
     */
    private void mapDtoToEntity(ZeroTillageParameterDto dto, ZeroTillageParameter entity) {
        entity.setCarbonIncreaseInSoil(dto.getCarbonIncreaseInSoil());
        entity.setCarbonToC02(dto.getCarbonToC02());
        entity.setEmissionFactorFromUrea(dto.getEmissionFactorFromUrea());
    }

    /**
     * Maps entity to response DTO
     */
    private ZeroTillageParameterResponseDto mapEntityToResponseDto(ZeroTillageParameter entity) {
        ZeroTillageParameterResponseDto dto = new ZeroTillageParameterResponseDto();
        dto.setId(entity.getId());
        dto.setCarbonIncreaseInSoil(entity.getCarbonIncreaseInSoil());
        dto.setCarbonToC02(entity.getCarbonToC02());
        dto.setEmissionFactorFromUrea(entity.getEmissionFactorFromUrea());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

