package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models.KigaliFSTPParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.repository.KigaliFSTPParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KigaliFSTPParameterServiceImpl implements KigaliFSTPParameterService {

    private final KigaliFSTPParameterRepository repository;

    @Override
    @Transactional
    public KigaliFSTPParameterResponseDto createKigaliFSTPParameter(KigaliFSTPParameterDto dto) {
        KigaliFSTPParameter parameter = new KigaliFSTPParameter();
        parameter.setMethaneEmissionFactor(dto.getMethaneEmissionFactor());
        parameter.setCodConcentration(dto.getCodConcentration());
        parameter.setCh4Gwp100Year(dto.getCh4Gwp100Year());
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        KigaliFSTPParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public KigaliFSTPParameterResponseDto updateKigaliFSTPParameter(UUID id, KigaliFSTPParameterDto dto) {
        KigaliFSTPParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kigali FSTP Parameter not found with id: " + id));

        parameter.setMethaneEmissionFactor(dto.getMethaneEmissionFactor());
        parameter.setCodConcentration(dto.getCodConcentration());
        parameter.setCh4Gwp100Year(dto.getCh4Gwp100Year());
        // Preserve isActive status on update
        
        KigaliFSTPParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public KigaliFSTPParameterResponseDto getKigaliFSTPParameterById(UUID id) {
        KigaliFSTPParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kigali FSTP Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KigaliFSTPParameterResponseDto> getAllKigaliFSTPParameters() {
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
    public void deleteKigaliFSTPParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Kigali FSTP Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        KigaliFSTPParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kigali FSTP Parameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public KigaliFSTPParameterResponseDto getLatestActive() {
        KigaliFSTPParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active Kigali FSTP Parameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps entity to response DTO
     */
    private KigaliFSTPParameterResponseDto mapEntityToResponseDto(KigaliFSTPParameter entity) {
        KigaliFSTPParameterResponseDto dto = new KigaliFSTPParameterResponseDto();
        dto.setId(entity.getId());
        dto.setMethaneEmissionFactor(entity.getMethaneEmissionFactor());
        dto.setCodConcentration(entity.getCodConcentration());
        dto.setCh4Gwp100Year(entity.getCh4Gwp100Year());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

