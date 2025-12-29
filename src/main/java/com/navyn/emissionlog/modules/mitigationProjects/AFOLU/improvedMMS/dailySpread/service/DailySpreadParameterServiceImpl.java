package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadParameter;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.repository.DailySpreadParameterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DailySpreadParameterServiceImpl implements DailySpreadParameterService {

    private final DailySpreadParameterRepository repository;

    @Override
    @Transactional
    public DailySpreadParameterResponseDto create(DailySpreadParameterDto dto) {
        DailySpreadParameter parameter = new DailySpreadParameter();
        mapDtoToEntity(dto, parameter);
        parameter.setIsActive(true); // New parameters are active by default
        DailySpreadParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DailySpreadParameterResponseDto getById(UUID id) {
        DailySpreadParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DailySpreadParameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailySpreadParameterResponseDto> getAll() {
        return repository.findAll().stream()
                .sorted((a, b) -> {
                    if (a.getIsActive() != b.getIsActive()) {
                        return b.getIsActive() ? 1 : -1; // true (active) comes first
                    }
                    return b.getCreatedAt().compareTo(a.getCreatedAt()); // Then sort by createdAt DESC
                })
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DailySpreadParameterResponseDto update(UUID id, DailySpreadParameterDto dto) {
        DailySpreadParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DailySpreadParameter not found with id: " + id));
        mapDtoToEntity(dto, parameter);
        DailySpreadParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        DailySpreadParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DailySpreadParameter not found with id: " + id));
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public DailySpreadParameterResponseDto getLatestActive() {
        DailySpreadParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active DailySpreadParameter found"));
        return mapEntityToResponseDto(latestActive);
    }

    private void mapDtoToEntity(DailySpreadParameterDto dto, DailySpreadParameter entity) {
        entity.setEmissionPerCow(dto.getEmissionPerCow());
        entity.setReduction(dto.getReduction());
    }

    private DailySpreadParameterResponseDto mapEntityToResponseDto(DailySpreadParameter entity) {
        DailySpreadParameterResponseDto dto = new DailySpreadParameterResponseDto();
        dto.setId(entity.getId());
        dto.setEmissionPerCow(entity.getEmissionPerCow());
        dto.setReduction(entity.getReduction());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

