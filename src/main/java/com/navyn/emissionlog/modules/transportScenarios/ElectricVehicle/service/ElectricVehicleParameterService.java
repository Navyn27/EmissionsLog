package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.service;

import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleParameterDto;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface ElectricVehicleParameterService {

    ElectricVehicleParameterResponseDto createElectricVehicleParameter(ElectricVehicleParameterDto dto);

    ElectricVehicleParameterResponseDto updateElectricVehicleParameter(UUID id, ElectricVehicleParameterDto dto);

    ElectricVehicleParameterResponseDto getElectricVehicleParameterById(UUID id);

    List<ElectricVehicleParameterResponseDto> getAllElectricVehicleParameters();

    ElectricVehicleParameterResponseDto getLatestActive();

    void disable(UUID id);
}

