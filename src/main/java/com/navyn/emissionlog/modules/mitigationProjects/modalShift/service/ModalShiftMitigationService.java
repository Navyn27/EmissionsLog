package com.navyn.emissionlog.modules.mitigationProjects.modalShift.service;

import com.navyn.emissionlog.modules.mitigationProjects.modalShift.dtos.ModalShiftMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.modalShift.dtos.ModalShiftMitigationResponseDto;

import java.util.List;
import java.util.UUID;

public interface ModalShiftMitigationService {
    
    ModalShiftMitigationResponseDto createModalShiftMitigation(ModalShiftMitigationDto dto);
    
    ModalShiftMitigationResponseDto updateModalShiftMitigation(UUID id, ModalShiftMitigationDto dto);
    
    void deleteModalShiftMitigation(UUID id);
    
    List<ModalShiftMitigationResponseDto> getAllModalShiftMitigation(Integer year);
}

