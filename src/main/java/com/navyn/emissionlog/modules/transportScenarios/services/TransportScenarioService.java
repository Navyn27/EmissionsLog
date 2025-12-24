package com.navyn.emissionlog.modules.transportScenarios.services;

import com.navyn.emissionlog.modules.transportScenarios.dtos.*;

import java.util.List;
import java.util.UUID;

public interface TransportScenarioService {

    // Scenario CRUD operations
    TransportScenarioResponseDto createScenario(TransportScenarioCreateDto dto);

    TransportScenarioResponseDto updateScenario(UUID id, TransportScenarioCreateDto dto);

    void deleteScenario(UUID id);

    TransportScenarioResponseDto getScenario(UUID id);

    List<TransportScenarioResponseDto> getAllScenarios();

    // Vehicle assumptions operations
    TransportScenarioVehicleAssumptionDto createOrUpdateVehicleAssumption(TransportScenarioVehicleAssumptionDto dto);

    List<TransportScenarioVehicleAssumptionDto> getVehicleAssumptionsForScenario(UUID scenarioId);

    void deleteVehicleAssumption(UUID assumptionId);

    // Global assumptions operations
    TransportScenarioYearGlobalAssumptionDto createOrUpdateGlobalAssumption(TransportScenarioYearGlobalAssumptionDto dto);

    List<TransportScenarioYearGlobalAssumptionDto> getGlobalAssumptionsForScenario(UUID scenarioId);

    void deleteGlobalAssumption(UUID assumptionId);

    // Modal shift assumptions operations
    TransportScenarioModalShiftAssumptionDto createOrUpdateModalShiftAssumption(TransportScenarioModalShiftAssumptionDto dto);

    List<TransportScenarioModalShiftAssumptionDto> getModalShiftAssumptionsForScenario(UUID scenarioId);

    void deleteModalShiftAssumption(UUID assumptionId);

    // Scenario execution
    TransportScenarioRunResponseDto runScenario(UUID scenarioId);
}
