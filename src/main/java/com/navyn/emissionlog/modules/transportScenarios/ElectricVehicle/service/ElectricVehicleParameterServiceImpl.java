package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.service;

import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleParameterDto;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleParameterResponseDto;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.models.ElectricVehicleParameter;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.repository.ElectricVehicleParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectricVehicleParameterServiceImpl implements ElectricVehicleParameterService {

    private final ElectricVehicleParameterRepository repository;

    @Override
    @Transactional
    public ElectricVehicleParameterResponseDto createElectricVehicleParameter(ElectricVehicleParameterDto dto) {
        ElectricVehicleParameter parameter = new ElectricVehicleParameter();
        parameter.setGridEmissionFactor(dto.getGridEmissionFactor());
        parameter.setIsActive(true);
        
        ElectricVehicleParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public ElectricVehicleParameterResponseDto updateElectricVehicleParameter(UUID id, ElectricVehicleParameterDto dto) {
        ElectricVehicleParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Electric Vehicle Parameter not found with id: " + id));

        parameter.setGridEmissionFactor(dto.getGridEmissionFactor());
        // Preserve isActive status on update
        
        ElectricVehicleParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ElectricVehicleParameterResponseDto getElectricVehicleParameterById(UUID id) {
        ElectricVehicleParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Electric Vehicle Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElectricVehicleParameterResponseDto> getAllElectricVehicleParameters() {
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
    public void disable(UUID id) {
        ElectricVehicleParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Electric Vehicle Parameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public ElectricVehicleParameterResponseDto getLatestActive() {
        ElectricVehicleParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active ElectricVehicleParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps entity to response DTO
     */
    private ElectricVehicleParameterResponseDto mapEntityToResponseDto(ElectricVehicleParameter entity) {
        ElectricVehicleParameterResponseDto dto = new ElectricVehicleParameterResponseDto();
        dto.setId(entity.getId());
        dto.setGridEmissionFactor(entity.getGridEmissionFactor());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

