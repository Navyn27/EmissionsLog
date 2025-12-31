package com.navyn.emissionlog.modules.transportScenarios.modalShift.service;

import com.navyn.emissionlog.modules.transportScenarios.modalShift.dtos.ModalShiftParameterDto;
import com.navyn.emissionlog.modules.transportScenarios.modalShift.dtos.ModalShiftParameterResponseDto;
import com.navyn.emissionlog.modules.transportScenarios.modalShift.models.ModalShiftParameter;
import com.navyn.emissionlog.modules.transportScenarios.modalShift.repository.ModalShiftParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModalShiftParameterServiceImpl implements ModalShiftParameterService {

    private final ModalShiftParameterRepository repository;

    @Override
    @Transactional
    public ModalShiftParameterResponseDto createModalShiftParameter(ModalShiftParameterDto dto) {
        ModalShiftParameter parameter = new ModalShiftParameter();
        parameter.setEnergyContentDiesel(dto.getEnergyContentDiesel());
        parameter.setEnergyContentGasoline(dto.getEnergyContentGasoline());
        parameter.setEmissionFactorCarbonDiesel(dto.getEmissionFactorCarbonDiesel());
        parameter.setEmissionFactorCarbonGasoline(dto.getEmissionFactorCarbonGasoline());
        parameter.setEmissionFactorMethaneDiesel(dto.getEmissionFactorMethaneDiesel());
        parameter.setEmissionFactorMethaneGasoline(dto.getEmissionFactorMethaneGasoline());
        parameter.setEmissionFactorNitrogenDiesel(dto.getEmissionFactorNitrogenDiesel());
        parameter.setEmissionFactorNitrogenGasoline(dto.getEmissionFactorNitrogenGasoline());
        parameter.setPotentialMethane(dto.getPotentialMethane());
        parameter.setPotentialNitrogen(dto.getPotentialNitrogen());
        // New parameters are active by default (isActive = true is set in entity)
        parameter.setIsActive(true);
        
        ModalShiftParameter saved = repository.save(parameter);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public ModalShiftParameterResponseDto updateModalShiftParameter(UUID id, ModalShiftParameterDto dto) {
        ModalShiftParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modal Shift Parameter not found with id: " + id));

        parameter.setEnergyContentDiesel(dto.getEnergyContentDiesel());
        parameter.setEnergyContentGasoline(dto.getEnergyContentGasoline());
        parameter.setEmissionFactorCarbonDiesel(dto.getEmissionFactorCarbonDiesel());
        parameter.setEmissionFactorCarbonGasoline(dto.getEmissionFactorCarbonGasoline());
        parameter.setEmissionFactorMethaneDiesel(dto.getEmissionFactorMethaneDiesel());
        parameter.setEmissionFactorMethaneGasoline(dto.getEmissionFactorMethaneGasoline());
        parameter.setEmissionFactorNitrogenDiesel(dto.getEmissionFactorNitrogenDiesel());
        parameter.setEmissionFactorNitrogenGasoline(dto.getEmissionFactorNitrogenGasoline());
        parameter.setPotentialMethane(dto.getPotentialMethane());
        parameter.setPotentialNitrogen(dto.getPotentialNitrogen());
        // Preserve isActive status on update
        
        ModalShiftParameter updated = repository.save(parameter);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ModalShiftParameterResponseDto getModalShiftParameterById(UUID id) {
        ModalShiftParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modal Shift Parameter not found with id: " + id));
        return mapEntityToResponseDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModalShiftParameterResponseDto> getAllModalShiftParameters() {
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
    public void deleteModalShiftParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Modal Shift Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void disable(UUID id) {
        ModalShiftParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modal Shift Parameter not found with id: " + id));
        
        parameter.setIsActive(false);
        repository.save(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public ModalShiftParameterResponseDto getLatestActive() {
        ModalShiftParameter latestActive = repository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No active ModalShiftParameter found"));
        
        return mapEntityToResponseDto(latestActive);
    }

    /**
     * Maps entity to response DTO
     */
    private ModalShiftParameterResponseDto mapEntityToResponseDto(ModalShiftParameter entity) {
        ModalShiftParameterResponseDto dto = new ModalShiftParameterResponseDto();
        dto.setId(entity.getId());
        dto.setEnergyContentDiesel(entity.getEnergyContentDiesel());
        dto.setEnergyContentGasoline(entity.getEnergyContentGasoline());
        dto.setEmissionFactorCarbonDiesel(entity.getEmissionFactorCarbonDiesel());
        dto.setEmissionFactorCarbonGasoline(entity.getEmissionFactorCarbonGasoline());
        dto.setEmissionFactorMethaneDiesel(entity.getEmissionFactorMethaneDiesel());
        dto.setEmissionFactorMethaneGasoline(entity.getEmissionFactorMethaneGasoline());
        dto.setEmissionFactorNitrogenDiesel(entity.getEmissionFactorNitrogenDiesel());
        dto.setEmissionFactorNitrogenGasoline(entity.getEmissionFactorNitrogenGasoline());
        dto.setPotentialMethane(entity.getPotentialMethane());
        dto.setPotentialNitrogen(entity.getPotentialNitrogen());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

