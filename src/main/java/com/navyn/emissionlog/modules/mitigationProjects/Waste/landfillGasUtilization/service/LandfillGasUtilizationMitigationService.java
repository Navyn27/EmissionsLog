package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasUtilizationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasUtilizationMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LandfillGasUtilizationMitigationService {

    LandfillGasUtilizationMitigationResponseDto createLandfillGasUtilizationMitigation(LandfillGasUtilizationMitigationDto dto);

    LandfillGasUtilizationMitigationResponseDto updateLandfillGasUtilizationMitigation(UUID id, LandfillGasUtilizationMitigationDto dto);

    void deleteLandfillGasUtilizationMitigation(UUID id);

    List<LandfillGasUtilizationMitigationResponseDto> getAllLandfillGasUtilizationMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createLandfillGasUtilizationMitigationFromExcel(MultipartFile file);
}
