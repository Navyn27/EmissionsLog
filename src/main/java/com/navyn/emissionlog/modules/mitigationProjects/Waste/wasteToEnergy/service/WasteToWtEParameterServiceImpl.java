package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToWtEParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToWtEParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToWtEParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.repository.WasteToWtEParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WasteToWtEParameterServiceImpl implements WasteToWtEParameterService {

    private final WasteToWtEParameterRepository repository;

    @Override
    @Transactional
    public WasteToWtEParameterResponseDto createWasteToWtEParameter(WasteToWtEParameterDto dto) {
        WasteToWtEParameter parameter = new WasteToWtEParameter();
        parameter.setNetEmissionFactor(dto.getNetEmissionFactor());
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        WasteToWtEParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public WasteToWtEParameterResponseDto updateWasteToWtEParameter(UUID id, WasteToWtEParameterDto dto) {
        WasteToWtEParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waste to WtE Parameter not found with id: " + id));

        parameter.setNetEmissionFactor(dto.getNetEmissionFactor());
        // Preserve isActive status on update
        
        WasteToWtEParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public WasteToWtEParameterResponseDto getWasteToWtEParameterById(UUID id) {
        WasteToWtEParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waste to WtE Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WasteToWtEParameterResponseDto> getAllWasteToWtEParameters() {
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
    public void deleteWasteToWtEParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Waste to WtE Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        WasteToWtEParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waste to WtE Parameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public WasteToWtEParameterResponseDto getLatestActive() {
        WasteToWtEParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active WasteToWtEParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps entity to response DTO
     */
    private WasteToWtEParameterResponseDto mapEntityToResponseDto(WasteToWtEParameter entity) {
        WasteToWtEParameterResponseDto dto = new WasteToWtEParameterResponseDto();
        dto.setId(entity.getId());
        dto.setNetEmissionFactor(entity.getNetEmissionFactor());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

