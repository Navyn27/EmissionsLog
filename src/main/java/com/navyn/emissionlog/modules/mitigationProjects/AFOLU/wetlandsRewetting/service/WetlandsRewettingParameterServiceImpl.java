package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.models.WetlandsRewettingParameter;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.repositories.WetlandsRewettingParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WetlandsRewettingParameterServiceImpl implements WetlandsRewettingParameterService {

    private final WetlandsRewettingParameterRepository repository;

    @Override
    @Transactional
    public WetlandsRewettingParameterResponseDto create(WetlandsRewettingParameterDto dto) {
        WetlandsRewettingParameter parameter = new WetlandsRewettingParameter();
        mapDtoToEntity(dto, parameter);
        parameter.setIsActive(true);
        WetlandsRewettingParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WetlandsRewettingParameterResponseDto getById(UUID id) {
        WetlandsRewettingParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WetlandsRewettingParameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WetlandsRewettingParameterResponseDto> getAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(WetlandsRewettingParameter::getIsActive, Comparator.reverseOrder())
                        .thenComparing(WetlandsRewettingParameter::getCreatedAt, Comparator.reverseOrder()))
                .map(this::mapEntityToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public WetlandsRewettingParameterResponseDto update(UUID id, WetlandsRewettingParameterDto dto) {
        WetlandsRewettingParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WetlandsRewettingParameter not found with id: " + id));
        mapDtoToEntity(dto, parameter);
        WetlandsRewettingParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        WetlandsRewettingParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WetlandsRewettingParameter not found with id: " + id));
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public WetlandsRewettingParameterResponseDto getLatestActive() {
        WetlandsRewettingParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active WetlandsRewettingParameter found"));
        return mapEntityToResponseDto(latestActive);
    }

    private void mapDtoToEntity(WetlandsRewettingParameterDto dto, WetlandsRewettingParameter entity) {
        entity.setCh4EmissionFactorPerHaPerYear(dto.getCh4EmissionFactorPerHaPerYear());
        entity.setGwpMethane(dto.getGwpMethane());
        entity.setCarbonSequestrationFactorPerHaPerYear(dto.getCarbonSequestrationFactorPerHaPerYear());
    }

    private WetlandsRewettingParameterResponseDto mapEntityToResponseDto(WetlandsRewettingParameter entity) {
        WetlandsRewettingParameterResponseDto dto = new WetlandsRewettingParameterResponseDto();
        dto.setId(entity.getId());
        dto.setCh4EmissionFactorPerHaPerYear(entity.getCh4EmissionFactorPerHaPerYear());
        dto.setGwpMethane(entity.getGwpMethane());
        dto.setCarbonSequestrationFactorPerHaPerYear(entity.getCarbonSequestrationFactorPerHaPerYear());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
