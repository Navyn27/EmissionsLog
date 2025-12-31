package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto.WaterHeatParameterDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto.WaterHeatParameterResponseDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.WaterHeatParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository.WaterHeatParameterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WaterHeatParameterService {

    private final WaterHeatParameterRepository repository;

    public WaterHeatParameterService(WaterHeatParameterRepository repository) {
        this.repository = repository;
    }

    /**
     * Maps WaterHeatParameter entity to Response DTO
     */
    private WaterHeatParameterResponseDTO toResponseDto(WaterHeatParameter param) {
        WaterHeatParameterResponseDTO dto = new WaterHeatParameterResponseDTO();
        dto.setId(param.getId());
        dto.setDeltaTemperature(param.getDeltaTemperature());
        dto.setSpecificHeatWater(param.getSpecificHeatWater());
        dto.setGridEmissionFactor(param.getGridEmissionFactor());
        // Handle null isActive - default to false for backward compatibility
        dto.setIsActive(param.getIsActive() != null ? param.getIsActive() : false);
        dto.setCreatedAt(param.getCreatedAt());
        dto.setUpdatedAt(param.getUpdatedAt());
        return dto;
    }

    public WaterHeatParameterResponseDTO create(WaterHeatParameterDTO dto) {
        WaterHeatParameter param = new WaterHeatParameter();
        param.setDeltaTemperature(dto.getDeltaTemperature());
        param.setSpecificHeatWater(dto.getSpecificHeatWater());
        param.setGridEmissionFactor(dto.getGridEmissionFactor());
        // New parameters are active by default (isActive = true is set in entity)
        param.setIsActive(true);
        
        WaterHeatParameter saved = repository.save(param);
        return toResponseDto(saved);
    }

    public List<WaterHeatParameterResponseDTO> getAll() {
        // Sort: active first (true), then by createdAt DESC (the latest first)
        return repository.findAll().stream()
                .sorted((a, b) -> {
                    // The first sort by isActive: true (active) comes first
                    // Handle null values - treat null as false (inactive)
                    // Use Boolean.TRUE.equals() to safely handle null values
                    boolean aIsActive = Boolean.TRUE.equals(a.getIsActive());
                    boolean bIsActive = Boolean.TRUE.equals(b.getIsActive());
                    
                    if (aIsActive != bIsActive) {
                        return bIsActive ? 1 : -1; // true (active) comes first
                    }
                    // Then sort by createdAt DESC (latest first)
                    // Handle null createdAt values
                    LocalDateTime aCreatedAt = a.getCreatedAt();
                    LocalDateTime bCreatedAt = b.getCreatedAt();
                    if (aCreatedAt == null && bCreatedAt == null) {
                        return 0;
                    }
                    if (aCreatedAt == null) {
                        return 1; // null comes last
                    }
                    if (bCreatedAt == null) {
                        return -1; // null comes last
                    }
                    return bCreatedAt.compareTo(aCreatedAt);
                })
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public WaterHeatParameterResponseDTO getById(UUID id) {
        WaterHeatParameter param = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WaterHeatParameter not found"));
        return toResponseDto(param);
    }

    public WaterHeatParameterResponseDTO update(UUID id, WaterHeatParameterDTO dto) {
        WaterHeatParameter existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WaterHeatParameter not found"));

        existing.setDeltaTemperature(dto.getDeltaTemperature());
        existing.setSpecificHeatWater(dto.getSpecificHeatWater());
        existing.setGridEmissionFactor(dto.getGridEmissionFactor());
        // Preserve isActive status on update

        WaterHeatParameter saved = repository.save(existing);
        return toResponseDto(saved);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    /**
     * Gets the latest active WaterHeatParameter (most recently created) as DTO
     * @return The latest active WaterHeatParameter as DTO
     * @throws RuntimeException if no active parameter is found
     */
    public WaterHeatParameterResponseDTO getLatestActive() {
        WaterHeatParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active WaterHeatParameter found. Please create a parameter first."));
        return toResponseDto(latestActive);
    }

    /**
     * Gets the latest active WaterHeatParameter (most recently created) as entity
     * @return The latest active WaterHeatParameter entity
     * @throws RuntimeException if no active parameter is found
     */
    public WaterHeatParameter getLatestActiveEntity() {
        return repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active WaterHeatParameter found. Please create a parameter first."));
    }

    /**
     * Disables a WaterHeatParameter by setting isActive to false
     * @param id The ID of the parameter to disable
     */
    public void disable(UUID id) {
        WaterHeatParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WaterHeatParameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }
}
