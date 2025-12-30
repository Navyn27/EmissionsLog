package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigationParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository.ISWMMitigationParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ISWMMitigationParameterServiceImpl implements ISWMParameterService {

    private final ISWMMitigationParameterRepository repository;

    @Override
    @Transactional
    public ISWMParameterResponseDto createISWMParameter(ISWMParameterDto dto) {
        ISWMMitigationParameter parameter = new ISWMMitigationParameter();
        parameter.setDegradableOrganicFraction(dto.getDegradableOrganicFraction());
        parameter.setLandfillAvoidance(dto.getLandfillAvoidance());
        parameter.setCompostingEF(dto.getCompostingEF());
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        ISWMMitigationParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public ISWMParameterResponseDto updateISWMParameter(UUID id, ISWMParameterDto dto) {
        ISWMMitigationParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ISWM Parameter not found with id: " + id));

        parameter.setDegradableOrganicFraction(dto.getDegradableOrganicFraction());
        parameter.setLandfillAvoidance(dto.getLandfillAvoidance());
        parameter.setCompostingEF(dto.getCompostingEF());
        // Preserve isActive status on update
        
        ISWMMitigationParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ISWMParameterResponseDto getISWMParameterById(UUID id) {
        ISWMMitigationParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ISWM Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ISWMParameterResponseDto> getAllISWMParameters() {
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
    public void deleteISWMParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("ISWM Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        ISWMMitigationParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ISWM Parameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public ISWMParameterResponseDto getLatestActive() {
        ISWMMitigationParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active ISWMMitigationParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps entity to response DTO
     */
    private ISWMParameterResponseDto mapEntityToResponseDto(ISWMMitigationParameter entity) {
        ISWMParameterResponseDto dto = new ISWMParameterResponseDto();
        dto.setId(entity.getId());
        dto.setDegradableOrganicFraction(entity.getDegradableOrganicFraction());
        dto.setLandfillAvoidance(entity.getLandfillAvoidance());
        dto.setCompostingEF(entity.getCompostingEF());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

