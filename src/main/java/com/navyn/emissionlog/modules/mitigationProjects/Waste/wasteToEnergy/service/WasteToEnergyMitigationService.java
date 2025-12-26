package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToEnergyMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToEnergyMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WasteToEnergyMitigationService {
    
    WasteToEnergyMitigationResponseDto createWasteToEnergyMitigation(WasteToEnergyMitigationDto dto);
    
    WasteToEnergyMitigationResponseDto updateWasteToEnergyMitigation(UUID id, WasteToEnergyMitigationDto dto);
    
    void deleteWasteToEnergyMitigation(UUID id);
    
    List<WasteToEnergyMitigationResponseDto> getAllWasteToEnergyMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createWasteToEnergyMitigationFromExcel(MultipartFile file);
}
