package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawParameter;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.repository.AddingStrawParameterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AddingStrawParameterServiceImpl implements AddingStrawParameterService {

    private final AddingStrawParameterRepository repository;

    @Override
    @Transactional
    public AddingStrawParameterResponseDto create(AddingStrawParameterDto dto) {
        AddingStrawParameter parameter = new AddingStrawParameter();
        mapDtoToEntity(dto, parameter);
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);

        AddingStrawParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AddingStrawParameterResponseDto getById(UUID id) {
        AddingStrawParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AddingStrawParameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddingStrawParameterResponseDto> getAll() {
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
    public AddingStrawParameterResponseDto update(UUID id, AddingStrawParameterDto dto) {
        AddingStrawParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AddingStrawParameter not found with id: " + id));

        mapDtoToEntity(dto, parameter);
        // Preserve isActive status on update
        AddingStrawParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        AddingStrawParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AddingStrawParameter not found with id: " + id));

        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public AddingStrawParameterResponseDto getLatestActive() {
        AddingStrawParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active AddingStrawParameter found"));

        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps DTO fields to entity
     */
    private void mapDtoToEntity(AddingStrawParameterDto dto, AddingStrawParameter entity) {
        entity.setEmissionPerCow(dto.getEmissionPerCow());
        entity.setReduction(dto.getReduction());
    }

    /**
     * Maps entity to response DTO
     */
    private AddingStrawParameterResponseDto mapEntityToResponseDto(AddingStrawParameter entity) {
        AddingStrawParameterResponseDto dto = new AddingStrawParameterResponseDto();
        dto.setId(entity.getId());
        dto.setEmissionPerCow(entity.getEmissionPerCow());
        dto.setReduction(entity.getReduction());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

