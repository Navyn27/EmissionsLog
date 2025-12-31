package com.navyn.emissionlog.modules.mitigationProjects.modalShift.service;

import com.navyn.emissionlog.modules.mitigationProjects.modalShift.dtos.ModalShiftParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.modalShift.dtos.ModalShiftParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface ModalShiftParameterService {

    ModalShiftParameterResponseDto createModalShiftParameter(ModalShiftParameterDto dto);

    ModalShiftParameterResponseDto updateModalShiftParameter(UUID id, ModalShiftParameterDto dto);

    ModalShiftParameterResponseDto getModalShiftParameterById(UUID id);

    List<ModalShiftParameterResponseDto> getAllModalShiftParameters();

    void deleteModalShiftParameter(UUID id);

    ModalShiftParameterResponseDto getLatestActive();

    void disable(UUID id);
}

