package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasParameter;

import java.util.List;
import java.util.UUID;

public interface LandfillGasParameterService {

    LandfillGasParameter createLandfillGasParameter(LandfillGasParameterDto dto);

    LandfillGasParameter updateLandfillGasParameter(UUID id, LandfillGasParameterDto dto);

    LandfillGasParameter getLandfillGasParameterById(UUID id);

    List<LandfillGasParameter> getAllLandfillGasParameters();

    void deleteLandfillGasParameter(UUID id);

    LandfillGasParameter getLatestLandfillGasParameter();
}
