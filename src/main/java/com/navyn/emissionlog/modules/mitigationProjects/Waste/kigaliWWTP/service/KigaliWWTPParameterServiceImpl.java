package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.repository.KigaliWWTPParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KigaliWWTPParameterServiceImpl implements KigaliWWTPParameterService {

    private final KigaliWWTPParameterRepository repository;

    @Override
    @Transactional
    public KigaliWWTPParameterResponseDto createKigaliWWTPParameter(KigaliWWTPParameterDto dto) {
        KigaliWWTPParameter parameter = new KigaliWWTPParameter();
        parameter.setMethaneEmissionFactor(dto.getMethaneEmissionFactor());
        parameter.setCodConcentration(dto.getCodConcentration());
        parameter.setCh4Gwp100Year(dto.getCh4Gwp100Year());
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        KigaliWWTPParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public KigaliWWTPParameterResponseDto updateKigaliWWTPParameter(UUID id, KigaliWWTPParameterDto dto) {
        KigaliWWTPParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kigali WWTP Parameter not found with id: " + id));

        parameter.setMethaneEmissionFactor(dto.getMethaneEmissionFactor());
        parameter.setCodConcentration(dto.getCodConcentration());
        parameter.setCh4Gwp100Year(dto.getCh4Gwp100Year());
        // Preserve isActive status on update
        
        KigaliWWTPParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public KigaliWWTPParameterResponseDto getKigaliWWTPParameterById(UUID id) {
        KigaliWWTPParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kigali WWTP Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KigaliWWTPParameterResponseDto> getAllKigaliWWTPParameters() {
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
    public void deleteKigaliWWTPParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Kigali WWTP Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        KigaliWWTPParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kigali WWTP Parameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public KigaliWWTPParameterResponseDto getLatestActive() {
        KigaliWWTPParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active KigaliWWTPParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps entity to response DTO
     */
    private KigaliWWTPParameterResponseDto mapEntityToResponseDto(KigaliWWTPParameter entity) {
        KigaliWWTPParameterResponseDto dto = new KigaliWWTPParameterResponseDto();
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

