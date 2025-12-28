package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.repository.LandfillGasParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LandfillGasParameterServiceImpl implements LandfillGasParameterService {

    private final LandfillGasParameterRepository repository;

    @Override
    @Transactional
    public LandfillGasParameterResponseDto createLandfillGasParameter(LandfillGasParameterDto dto) {
        LandfillGasParameter parameter = new LandfillGasParameter();
        parameter.setDestructionEfficiencyPercentage(dto.getDestructionEfficiencyPercentage());
        parameter.setGlobalWarmingPotentialCh4(dto.getGlobalWarmingPotentialCh4());
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        LandfillGasParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public LandfillGasParameterResponseDto updateLandfillGasParameter(UUID id, LandfillGasParameterDto dto) {
        LandfillGasParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Landfill Gas Parameter not found with id: " + id));

        parameter.setDestructionEfficiencyPercentage(dto.getDestructionEfficiencyPercentage());
        parameter.setGlobalWarmingPotentialCh4(dto.getGlobalWarmingPotentialCh4());
        // Preserve isActive status on update
        
        LandfillGasParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public LandfillGasParameterResponseDto getLandfillGasParameterById(UUID id) {
        LandfillGasParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Landfill Gas Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LandfillGasParameterResponseDto> getAllLandfillGasParameters() {
        // Sort: active first (true), then by createdAt DESC (the latest first)
        return repository.findAll().stream()
                .sorted((a, b) -> {
                    // The first sort by isActive: true (active) comes first
                    // Handle null values - treat null as false (inactive)
                    boolean aIsActive = a.getIsActive() != null && a.getIsActive();
                    boolean bIsActive = b.getIsActive() != null && b.getIsActive();
                    
                    if (aIsActive != bIsActive) {
                        return bIsActive ? 1 : -1; // true (active) comes first
                    }
                    // Then sort by createdAt DESC (latest first)
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteLandfillGasParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Landfill Gas Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        LandfillGasParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Landfill Gas Parameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public LandfillGasParameterResponseDto getLatestActive() {
        LandfillGasParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active LandfillGasParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps entity to response DTO
     */
    private LandfillGasParameterResponseDto mapEntityToResponseDto(LandfillGasParameter entity) {
        LandfillGasParameterResponseDto dto = new LandfillGasParameterResponseDto();
        dto.setId(entity.getId());
        dto.setDestructionEfficiencyPercentage(entity.getDestructionEfficiencyPercentage());
        dto.setGlobalWarmingPotentialCh4(entity.getGlobalWarmingPotentialCh4());
        // Handle null isActive - default to false for backward compatibility
        dto.setIsActive(entity.getIsActive() != null ? entity.getIsActive() : false);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
