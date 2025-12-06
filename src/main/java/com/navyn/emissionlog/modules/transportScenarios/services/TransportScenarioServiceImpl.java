package com.navyn.emissionlog.modules.transportScenarios.services;

import com.navyn.emissionlog.modules.transportScenarios.calculations.TransportScenarioCalculator;
import com.navyn.emissionlog.modules.transportScenarios.dtos.*;
import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenario;
import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenarioModalShiftAssumption;
import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenarioVehicleAssumption;
import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenarioYearGlobalAssumption;
import com.navyn.emissionlog.modules.transportScenarios.repositories.TransportScenarioModalShiftAssumptionRepository;
import com.navyn.emissionlog.modules.transportScenarios.repositories.TransportScenarioRepository;
import com.navyn.emissionlog.modules.transportScenarios.repositories.TransportScenarioVehicleAssumptionRepository;
import com.navyn.emissionlog.modules.transportScenarios.repositories.TransportScenarioYearGlobalAssumptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransportScenarioServiceImpl implements TransportScenarioService {

    private final TransportScenarioRepository scenarioRepository;
    private final TransportScenarioVehicleAssumptionRepository vehicleAssumptionRepository;
    private final TransportScenarioYearGlobalAssumptionRepository globalAssumptionRepository;
    private final TransportScenarioModalShiftAssumptionRepository modalShiftAssumptionRepository;
    private final TransportScenarioCalculator calculator;

    public TransportScenarioServiceImpl(
            TransportScenarioRepository scenarioRepository,
            TransportScenarioVehicleAssumptionRepository vehicleAssumptionRepository,
            TransportScenarioYearGlobalAssumptionRepository globalAssumptionRepository,
            TransportScenarioModalShiftAssumptionRepository modalShiftAssumptionRepository,
            TransportScenarioCalculator calculator) {
        this.scenarioRepository = scenarioRepository;
        this.vehicleAssumptionRepository = vehicleAssumptionRepository;
        this.globalAssumptionRepository = globalAssumptionRepository;
        this.modalShiftAssumptionRepository = modalShiftAssumptionRepository;
        this.calculator = calculator;
    }

    // ======================== Scenario CRUD ========================

    @Override
    @Transactional
    public TransportScenarioResponseDto createScenario(TransportScenarioCreateDto dto) {
        // Validate unique name
        if (scenarioRepository.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("A scenario with name '" + dto.getName() + "' already exists");
        }

        // Validate base year < end year
        if (dto.getBaseYear() >= dto.getEndYear()) {
            throw new IllegalArgumentException("Base year must be before end year");
        }

        TransportScenario scenario = new TransportScenario();
        scenario.setName(dto.getName());
        scenario.setDescription(dto.getDescription());
        scenario.setBaseYear(dto.getBaseYear());
        scenario.setEndYear(dto.getEndYear());

        if (!scenario.isValid()) {
            throw new IllegalArgumentException("Invalid scenario data");
        }

        TransportScenario saved = scenarioRepository.save(scenario);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public TransportScenarioResponseDto updateScenario(UUID id, TransportScenarioCreateDto dto) {
        TransportScenario existing = scenarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + id));

        // Check name uniqueness if changing
        if (!existing.getName().equals(dto.getName())) {
            if (scenarioRepository.findByName(dto.getName()).isPresent()) {
                throw new IllegalArgumentException("A scenario with name '" + dto.getName() + "' already exists");
            }
        }

        // Validate base year < end year
        if (dto.getBaseYear() >= dto.getEndYear()) {
            throw new IllegalArgumentException("Base year must be before end year");
        }

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setBaseYear(dto.getBaseYear());
        existing.setEndYear(dto.getEndYear());

        if (!existing.isValid()) {
            throw new IllegalArgumentException("Invalid scenario data");
        }

        TransportScenario updated = scenarioRepository.save(existing);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteScenario(UUID id) {
        if (!scenarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Scenario not found with id: " + id);
        }
        scenarioRepository.deleteById(id);
    }

    @Override
    public TransportScenarioResponseDto getScenario(UUID id) {
        TransportScenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + id));
        return mapToResponseDto(scenario);
    }

    @Override
    public List<TransportScenarioResponseDto> getAllScenarios() {
        return scenarioRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // ==================== Vehicle Assumptions ====================

    @Override
    @Transactional
    public TransportScenarioVehicleAssumptionDto createOrUpdateVehicleAssumption(
            TransportScenarioVehicleAssumptionDto dto) {

        // Validate scenario exists
        TransportScenario scenario = scenarioRepository.findById(dto.getScenarioId())
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + dto.getScenarioId()));

        TransportScenarioVehicleAssumption assumption;

        if (dto.getId() != null) {
            // Update existing
            assumption = vehicleAssumptionRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle assumption not found with id: " + dto.getId()));
        } else {
            // Check for existing with same scenario/year/category
            assumption = vehicleAssumptionRepository
                    .findByScenarioIdAndYearAndVehicleCategory(
                            dto.getScenarioId(), dto.getYear(), dto.getVehicleCategory())
                    .orElse(new TransportScenarioVehicleAssumption());
        }

        // Set all fields
        assumption.setScenario(scenario);
        assumption.setYear(dto.getYear());
        assumption.setVehicleCategory(dto.getVehicleCategory());
        assumption.setFuelType(dto.getFuelType());
        assumption.setFleetSize(dto.getFleetSize());
        assumption.setAverageKmPerVehicle(dto.getAverageKmPerVehicle());
        assumption.setFuelEconomyLPer100Km(dto.getFuelEconomyLPer100Km());
        assumption.setEvShare(dto.getEvShare());
        assumption.setEvKWhPer100Km(dto.getEvKWhPer100Km());

        if (!assumption.isValid()) {
            throw new IllegalArgumentException("Invalid vehicle assumption data");
        }

        TransportScenarioVehicleAssumption saved = vehicleAssumptionRepository.save(assumption);
        return mapToVehicleAssumptionDto(saved);
    }

    @Override
    public List<TransportScenarioVehicleAssumptionDto> getVehicleAssumptionsForScenario(UUID scenarioId) {
        return vehicleAssumptionRepository.findByScenarioId(scenarioId).stream()
                .map(this::mapToVehicleAssumptionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteVehicleAssumption(UUID assumptionId) {
        if (!vehicleAssumptionRepository.existsById(assumptionId)) {
            throw new IllegalArgumentException("Vehicle assumption not found with id: " + assumptionId);
        }
        vehicleAssumptionRepository.deleteById(assumptionId);
    }

    // ==================== Global Assumptions ====================

    @Override
    @Transactional
    public TransportScenarioYearGlobalAssumptionDto createOrUpdateGlobalAssumption(
            TransportScenarioYearGlobalAssumptionDto dto) {

        // Validate scenario exists
        TransportScenario scenario = scenarioRepository.findById(dto.getScenarioId())
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + dto.getScenarioId()));

        TransportScenarioYearGlobalAssumption assumption;

        if (dto.getId() != null) {
            // Update existing
            assumption = globalAssumptionRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Global assumption not found with id: " + dto.getId()));
        } else {
            // Check for existing with same scenario/year
            assumption = globalAssumptionRepository
                    .findByScenarioIdAndYear(dto.getScenarioId(), dto.getYear())
                    .orElse(new TransportScenarioYearGlobalAssumption());
        }

        // Set all fields
        assumption.setScenario(scenario);
        assumption.setYear(dto.getYear());
        assumption.setFuelEmissionFactorTco2PerTJ_Gasoline(dto.getFuelEmissionFactorTco2PerTJ_Gasoline());
        assumption.setFuelEmissionFactorTco2PerTJ_Diesel(dto.getFuelEmissionFactorTco2PerTJ_Diesel());
        assumption.setFuelEnergyDensityTjPerL_Gasoline(dto.getFuelEnergyDensityTjPerL_Gasoline());
        assumption.setFuelEnergyDensityTjPerL_Diesel(dto.getFuelEnergyDensityTjPerL_Diesel());
        assumption.setGridEmissionFactorTco2PerMWh(dto.getGridEmissionFactorTco2PerMWh());

        if (!assumption.isValid()) {
            throw new IllegalArgumentException("Invalid global assumption data");
        }

        TransportScenarioYearGlobalAssumption saved = globalAssumptionRepository.save(assumption);
        return mapToGlobalAssumptionDto(saved);
    }

    @Override
    public List<TransportScenarioYearGlobalAssumptionDto> getGlobalAssumptionsForScenario(UUID scenarioId) {
        return globalAssumptionRepository.findByScenarioId(scenarioId).stream()
                .map(this::mapToGlobalAssumptionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteGlobalAssumption(UUID assumptionId) {
        if (!globalAssumptionRepository.existsById(assumptionId)) {
            throw new IllegalArgumentException("Global assumption not found with id: " + assumptionId);
        }
        globalAssumptionRepository.deleteById(assumptionId);
    }

    // ==================== Modal Shift Assumptions ====================

    @Override
    @Transactional
    public TransportScenarioModalShiftAssumptionDto createOrUpdateModalShiftAssumption(
            TransportScenarioModalShiftAssumptionDto dto) {

        // Validate scenario exists
        TransportScenario scenario = scenarioRepository.findById(dto.getScenarioId())
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + dto.getScenarioId()));

        TransportScenarioModalShiftAssumption assumption;

        if (dto.getId() != null) {
            // Update existing
            assumption = modalShiftAssumptionRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Modal shift assumption not found with id: " + dto.getId()));
        } else {
            // Check for existing with same scenario/year
            assumption = modalShiftAssumptionRepository
                    .findByScenarioIdAndYear(dto.getScenarioId(), dto.getYear())
                    .orElse(new TransportScenarioModalShiftAssumption());
        }

        // Set all fields
        assumption.setScenario(scenario);
        assumption.setYear(dto.getYear());
        assumption.setPassengerKmMotorcycleBau(dto.getPassengerKmMotorcycleBau());
        assumption.setPassengerKmCarBau(dto.getPassengerKmCarBau());
        assumption.setPassengerKmBusBau(dto.getPassengerKmBusBau());
        assumption.setEmissionFactorMotorcycle_gPerPassKm(dto.getEmissionFactorMotorcycle_gPerPassKm());
        assumption.setEmissionFactorCar_gPerPassKm(dto.getEmissionFactorCar_gPerPassKm());
        assumption.setEmissionFactorBus_gPerPassKm(dto.getEmissionFactorBus_gPerPassKm());
        assumption.setShiftFractionMotorcycleToBus(dto.getShiftFractionMotorcycleToBus());
        assumption.setShiftFractionCarToBus(dto.getShiftFractionCarToBus());

        if (!assumption.isValid()) {
            throw new IllegalArgumentException("Invalid modal shift assumption data");
        }

        TransportScenarioModalShiftAssumption saved = modalShiftAssumptionRepository.save(assumption);
        return mapToModalShiftAssumptionDto(saved);
    }

    @Override
    public List<TransportScenarioModalShiftAssumptionDto> getModalShiftAssumptionsForScenario(UUID scenarioId) {
        return modalShiftAssumptionRepository.findByScenarioId(scenarioId).stream()
                .map(this::mapToModalShiftAssumptionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteModalShiftAssumption(UUID assumptionId) {
        if (!modalShiftAssumptionRepository.existsById(assumptionId)) {
            throw new IllegalArgumentException("Modal shift assumption not found with id: " + assumptionId);
        }
        modalShiftAssumptionRepository.deleteById(assumptionId);
    }

    // ==================== Scenario Execution ====================

    @Override
    public TransportScenarioRunResponseDto runScenario(UUID scenarioId) {
        TransportScenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + scenarioId));

        List<TransportScenarioVehicleAssumption> vehicleAssumptions =
                vehicleAssumptionRepository.findByScenarioId(scenarioId);

        List<TransportScenarioYearGlobalAssumption> globalAssumptions =
                globalAssumptionRepository.findByScenarioId(scenarioId);

        List<TransportScenarioModalShiftAssumption> modalShiftAssumptions =
                modalShiftAssumptionRepository.findByScenarioId(scenarioId);

        return calculator.calculate(scenario, vehicleAssumptions, globalAssumptions, modalShiftAssumptions);
    }

    // ==================== Mapping Methods ====================

    private TransportScenarioResponseDto mapToResponseDto(TransportScenario entity) {
        TransportScenarioResponseDto dto = new TransportScenarioResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setBaseYear(entity.getBaseYear());
        dto.setEndYear(entity.getEndYear());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private TransportScenarioVehicleAssumptionDto mapToVehicleAssumptionDto(
            TransportScenarioVehicleAssumption entity) {
        TransportScenarioVehicleAssumptionDto dto = new TransportScenarioVehicleAssumptionDto();
        dto.setId(entity.getId());
        dto.setScenarioId(entity.getScenario().getId());
        dto.setYear(entity.getYear());
        dto.setVehicleCategory(entity.getVehicleCategory());
        dto.setFuelType(entity.getFuelType());
        dto.setFleetSize(entity.getFleetSize());
        dto.setAverageKmPerVehicle(entity.getAverageKmPerVehicle());
        dto.setFuelEconomyLPer100Km(entity.getFuelEconomyLPer100Km());
        dto.setEvShare(entity.getEvShare());
        dto.setEvKWhPer100Km(entity.getEvKWhPer100Km());
        return dto;
    }

    private TransportScenarioYearGlobalAssumptionDto mapToGlobalAssumptionDto(
            TransportScenarioYearGlobalAssumption entity) {
        TransportScenarioYearGlobalAssumptionDto dto = new TransportScenarioYearGlobalAssumptionDto();
        dto.setId(entity.getId());
        dto.setScenarioId(entity.getScenario().getId());
        dto.setYear(entity.getYear());
        dto.setFuelEmissionFactorTco2PerTJ_Gasoline(entity.getFuelEmissionFactorTco2PerTJ_Gasoline());
        dto.setFuelEmissionFactorTco2PerTJ_Diesel(entity.getFuelEmissionFactorTco2PerTJ_Diesel());
        dto.setFuelEnergyDensityTjPerL_Gasoline(entity.getFuelEnergyDensityTjPerL_Gasoline());
        dto.setFuelEnergyDensityTjPerL_Diesel(entity.getFuelEnergyDensityTjPerL_Diesel());
        dto.setGridEmissionFactorTco2PerMWh(entity.getGridEmissionFactorTco2PerMWh());
        return dto;
    }

    private TransportScenarioModalShiftAssumptionDto mapToModalShiftAssumptionDto(
            TransportScenarioModalShiftAssumption entity) {
        TransportScenarioModalShiftAssumptionDto dto = new TransportScenarioModalShiftAssumptionDto();
        dto.setId(entity.getId());
        dto.setScenarioId(entity.getScenario().getId());
        dto.setYear(entity.getYear());
        dto.setPassengerKmMotorcycleBau(entity.getPassengerKmMotorcycleBau());
        dto.setPassengerKmCarBau(entity.getPassengerKmCarBau());
        dto.setPassengerKmBusBau(entity.getPassengerKmBusBau());
        dto.setEmissionFactorMotorcycle_gPerPassKm(entity.getEmissionFactorMotorcycle_gPerPassKm());
        dto.setEmissionFactorCar_gPerPassKm(entity.getEmissionFactorCar_gPerPassKm());
        dto.setEmissionFactorBus_gPerPassKm(entity.getEmissionFactorBus_gPerPassKm());
        dto.setShiftFractionMotorcycleToBus(entity.getShiftFractionMotorcycleToBus());
        dto.setShiftFractionCarToBus(entity.getShiftFractionCarToBus());
        return dto;
    }
}
