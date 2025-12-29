package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models.EPRParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.repository.EPRParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EPRParameterServiceImpl implements EPRParameterService {

    private final EPRParameterRepository repository;

    @Override
    @Transactional
    public EPRParameterResponseDto createEPRParameter(EPRParameterDto dto) {
        EPRParameter parameter = new EPRParameter();
        parameter.setRecyclingRateWithoutEPR(dto.getRecyclingRateWithoutEPR());
        parameter.setRecyclingRateWithEPR(dto.getRecyclingRateWithEPR());
        parameter.setEmissionFactor(dto.getEmissionFactor());
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        EPRParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public EPRParameterResponseDto updateEPRParameter(UUID id, EPRParameterDto dto) {
        EPRParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("EPR Parameter not found with id: " + id));

        parameter.setRecyclingRateWithoutEPR(dto.getRecyclingRateWithoutEPR());
        parameter.setRecyclingRateWithEPR(dto.getRecyclingRateWithEPR());
        parameter.setEmissionFactor(dto.getEmissionFactor());
        // Preserve isActive status on update
        
        EPRParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EPRParameterResponseDto getEPRParameterById(UUID id) {
        EPRParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("EPR Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EPRParameterResponseDto> getAllEPRParameters() {
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
    public void deleteEPRParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("EPR Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        EPRParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("EPR Parameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public EPRParameterResponseDto getLatestActive() {
        EPRParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active EPRParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps entity to response DTO
     */
    private EPRParameterResponseDto mapEntityToResponseDto(EPRParameter entity) {
        EPRParameterResponseDto dto = new EPRParameterResponseDto();
        dto.setId(entity.getId());
        dto.setRecyclingRateWithoutEPR(entity.getRecyclingRateWithoutEPR());
        dto.setRecyclingRateWithEPR(entity.getRecyclingRateWithEPR());
        dto.setEmissionFactor(entity.getEmissionFactor());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

