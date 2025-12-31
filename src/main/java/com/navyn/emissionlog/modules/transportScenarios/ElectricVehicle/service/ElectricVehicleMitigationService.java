package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.service;

import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleMitigationDto;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleMitigationResponseDto;

import java.util.List;
import java.util.UUID;

public interface ElectricVehicleMitigationService {
    
    ElectricVehicleMitigationResponseDto createElectricVehicleMitigation(ElectricVehicleMitigationDto dto);
    
    ElectricVehicleMitigationResponseDto updateElectricVehicleMitigation(UUID id, ElectricVehicleMitigationDto dto);
    
    void deleteElectricVehicleMitigation(UUID id);
    
    List<ElectricVehicleMitigationResponseDto> getAllElectricVehicleMitigation(Integer year);
}

