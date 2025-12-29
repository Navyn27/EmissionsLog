package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringParameter;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.repository.ManureCoveringParameterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ManureCoveringParameterServiceImpl implements ManureCoveringParameterService {

    private final ManureCoveringParameterRepository repository;

    @Override
    @Transactional
    public ManureCoveringParameterResponseDto create(ManureCoveringParameterDto dto) {
        ManureCoveringParameter parameter = new ManureCoveringParameter();
        mapDtoToEntity(dto, parameter);
        parameter.setIsActive(true); // New parameters are active by default
        ManureCoveringParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ManureCoveringParameterResponseDto getById(UUID id) {
        ManureCoveringParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ManureCoveringParameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManureCoveringParameterResponseDto> getAll() {
        return repository.findAll().stream()
                .sorted((a, b) -> {
                    if (a.getIsActive() != b.getIsActive()) {
                        return b.getIsActive() ? 1 : -1; // true (active) comes first
                    }
                    return b.getCreatedAt().compareTo(a.getCreatedAt()); // Then sort by createdAt DESC
                })
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ManureCoveringParameterResponseDto update(UUID id, ManureCoveringParameterDto dto) {
        ManureCoveringParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ManureCoveringParameter not found with id: " + id));
        mapDtoToEntity(dto, parameter);
        ManureCoveringParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        ManureCoveringParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ManureCoveringParameter not found with id: " + id));
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public ManureCoveringParameterResponseDto getLatestActive() {
        ManureCoveringParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active ManureCoveringParameter found"));
        return mapEntityToResponseDto(latestActive);
    }

    private void mapDtoToEntity(ManureCoveringParameterDto dto, ManureCoveringParameter entity) {
        entity.setEmissionPerCow(dto.getEmissionPerCow());
        entity.setReduction(dto.getReduction());
    }

    private ManureCoveringParameterResponseDto mapEntityToResponseDto(ManureCoveringParameter entity) {
        ManureCoveringParameterResponseDto dto = new ManureCoveringParameterResponseDto();
        dto.setId(entity.getId());
        dto.setEmissionPerCow(entity.getEmissionPerCow());
        dto.setReduction(entity.getReduction());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

