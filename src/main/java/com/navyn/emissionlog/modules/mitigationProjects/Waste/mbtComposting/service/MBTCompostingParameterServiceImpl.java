package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.models.MBTCompostingParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.repository.MBTCompostingParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MBTCompostingParameterServiceImpl implements MBTCompostingParameterService {

    private final MBTCompostingParameterRepository repository;

    @Override
    @Transactional
    public MBTCompostingParameterResponseDto createMBTCompostingParameter(MBTCompostingParameterDto dto) {
        MBTCompostingParameter parameter = new MBTCompostingParameter();
        parameter.setEmissionFactor(dto.getEmissionFactor());
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        MBTCompostingParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public MBTCompostingParameterResponseDto updateMBTCompostingParameter(UUID id, MBTCompostingParameterDto dto) {
        MBTCompostingParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("MBT Composting Parameter not found with id: " + id));

        parameter.setEmissionFactor(dto.getEmissionFactor());
        // Preserve isActive status on update
        
        MBTCompostingParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public MBTCompostingParameterResponseDto getMBTCompostingParameterById(UUID id) {
        MBTCompostingParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("MBT Composting Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MBTCompostingParameterResponseDto> getAllMBTCompostingParameters() {
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
    public void deleteMBTCompostingParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("MBT Composting Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        MBTCompostingParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("MBT Composting Parameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public MBTCompostingParameterResponseDto getLatestActive() {
        MBTCompostingParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active MBTCompostingParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps entity to response DTO
     */
    private MBTCompostingParameterResponseDto mapEntityToResponseDto(MBTCompostingParameter entity) {
        MBTCompostingParameterResponseDto dto = new MBTCompostingParameterResponseDto();
        dto.setId(entity.getId());
        dto.setEmissionFactor(entity.getEmissionFactor());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

