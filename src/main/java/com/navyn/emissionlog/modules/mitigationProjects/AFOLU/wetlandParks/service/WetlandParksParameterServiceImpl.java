package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models.WetlandParksParameter;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.repositories.WetlandParksParameterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WetlandParksParameterServiceImpl implements WetlandParksParameterService {

    private final WetlandParksParameterRepository repository;

    @Override
    @Transactional
    public WetlandParksParameterResponseDto create(WetlandParksParameterDto dto) {
        WetlandParksParameter parameter = new WetlandParksParameter();
        mapDtoToEntity(dto, parameter);
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);

        WetlandParksParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WetlandParksParameterResponseDto getById(UUID id) {
        WetlandParksParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WetlandParksParameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WetlandParksParameterResponseDto> getAll() {
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
    public WetlandParksParameterResponseDto update(UUID id, WetlandParksParameterDto dto) {
        WetlandParksParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WetlandParksParameter not found with id: " + id));

        mapDtoToEntity(dto, parameter);
        // Preserve isActive status on update
        WetlandParksParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        WetlandParksParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WetlandParksParameter not found with id: " + id));

        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public WetlandParksParameterResponseDto getLatestActive() {
        WetlandParksParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active WetlandParksParameter found"));

        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps DTO fields to entity
     */
    private void mapDtoToEntity(WetlandParksParameterDto dto, WetlandParksParameter entity) {
        entity.setConversionM3ToTonnesDM(dto.getConversionM3ToTonnesDM());
        entity.setRatioOfBelowGroundBiomass(dto.getRatioOfBelowGroundBiomass());
        entity.setCarbonContentDryWood(dto.getCarbonContentDryWood());
        entity.setCarbonToC02(dto.getCarbonToC02());
    }

    /**
     * Maps entity to response DTO
     */
    private WetlandParksParameterResponseDto mapEntityToResponseDto(WetlandParksParameter entity) {
        WetlandParksParameterResponseDto dto = new WetlandParksParameterResponseDto();
        dto.setId(entity.getId());
        dto.setConversionM3ToTonnesDM(entity.getConversionM3ToTonnesDM());
        dto.setRatioOfBelowGroundBiomass(entity.getRatioOfBelowGroundBiomass());
        dto.setCarbonContentDryWood(entity.getCarbonContentDryWood());
        dto.setCarbonToC02(entity.getCarbonToC02());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

