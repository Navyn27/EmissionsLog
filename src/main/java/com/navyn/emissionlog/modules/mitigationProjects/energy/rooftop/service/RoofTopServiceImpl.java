package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.service;

import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.services.BAUService;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model.RoofTopMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model.RoofTopParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.repository.IRoofTopMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.repository.IRoofTopParameterRepository;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoofTopServiceImpl implements IRoofTopMitigationService {

    private final IRoofTopMitigationRepository mitigationRepository;
    private final IRoofTopParameterRepository parameterRepository;
    private final IRoofTopParameterService parameterService;
    private final InterventionRepository interventionRepository;
    private final BAUService bauService;

    private static final double DIESEL_EMISSION_FACTOR = 74.1;

    @Override
    @Transactional
    public RoofTopMitigationResponseDto create(RoofTopMitigationDto dto) {
        // Get Intervention
        Intervention intervention = interventionRepository.findById(dto.getProjectInterventionId())
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getProjectInterventionId()));

        // Get BAU for Energy sector and same year
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(dto.getYear(), ESector.ENERGY);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + dto.getYear() + " and sector ENERGY. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();

        // Get latest parameters
        RoofTopParameter parameter = getLatestParameter();

        RoofTopMitigation mitigation = new RoofTopMitigation();
        mitigation.setYear(dto.getYear());
        mitigation.setInstalledUnitPerYear(dto.getInstalledUnitPerYear());
        mitigation.setSolarPVCapacity(dto.getSolarPVCapacity());
        mitigation.setProjectIntervention(intervention);

        // Calculate all fields
        calculateAllFields(mitigation, parameter, bau);

        RoofTopMitigation saved = mitigationRepository.save(mitigation);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public RoofTopMitigationResponseDto getById(UUID id) {
        RoofTopMitigation mitigation = mitigationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RoofTopMitigation not found with id: " + id));

        // Get BAU for Energy sector and same year
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(mitigation.getYear(), ESector.ENERGY);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + mitigation.getYear() + " and sector ENERGY. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();

        RoofTopParameter parameter = getLatestParameter();
        calculateAllFields(mitigation, parameter, bau);

        return mapEntityToResponseDto(mitigation);
    }

    @Override
    @Transactional
    public List<RoofTopMitigationResponseDto> getAll() {
        RoofTopParameter parameter = getLatestParameter();

        return mitigationRepository.findAllByOrderByYearAsc().stream()
                .map(mitigation -> {
                    // Get BAU for Energy sector and same year
                    Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(mitigation.getYear(), ESector.ENERGY);
                    if (bauOptional.isEmpty()) {
                        throw new RuntimeException("BAU record not found for year " + mitigation.getYear() + " and sector ENERGY. Please create BAU record first.");
                    }
                    BAU bau = bauOptional.get();
                    calculateAllFields(mitigation, parameter, bau);
                    return mapEntityToResponseDto(mitigation);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoofTopMitigationResponseDto update(UUID id, RoofTopMitigationDto dto) {
        RoofTopMitigation mitigation = mitigationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RoofTopMitigation not found with id: " + id));

        // Get Intervention
        Intervention intervention = interventionRepository.findById(dto.getProjectInterventionId())
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getProjectInterventionId()));

        // Get BAU for Energy sector and same year
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(dto.getYear(), ESector.ENERGY);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + dto.getYear() + " and sector ENERGY. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();

        mitigation.setYear(dto.getYear());
        mitigation.setInstalledUnitPerYear(dto.getInstalledUnitPerYear());
        mitigation.setSolarPVCapacity(dto.getSolarPVCapacity());
        mitigation.setProjectIntervention(intervention);

        RoofTopParameter parameter = getLatestParameter();
        calculateAllFields(mitigation, parameter, bau);

        // Recalculate all subsequent years if cumulative values changed
        recalculateSubsequentYears(mitigation.getYear());

        RoofTopMitigation updated = mitigationRepository.save(mitigation);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        RoofTopMitigation mitigation = mitigationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RoofTopMitigation not found with id: " + id));

        int year = mitigation.getYear();
        mitigationRepository.deleteById(id);

        // Recalculate all subsequent years
        recalculateSubsequentYears(year);
    }

    @Override
    @Transactional
    public List<RoofTopMitigationResponseDto> getByYear(int year) {
        List<RoofTopMitigation> mitigations = mitigationRepository.findAllByYear(year);
        if (mitigations.isEmpty()) {
            return Collections.emptyList();
        }

        // Get BAU for Energy sector and same year
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(year, ESector.ENERGY);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + year + " and sector ENERGY. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();

        RoofTopParameter parameter = getLatestParameter();
        return mitigations.stream()
                .map(mitigation -> {
                    calculateAllFields(mitigation, parameter, bau);
                    return mapEntityToResponseDto(mitigation);
                })
                .collect(Collectors.toList());
    }

    private RoofTopParameter getLatestParameter() {
        return parameterRepository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new EntityNotFoundException("No active RoofTopParameter found. Please create an active parameter first."));
    }

    private void calculateAllFields(RoofTopMitigation mitigation, RoofTopParameter parameter, BAU bau) {
        // Set BAU emission (transient, from BAU table)
        mitigation.setBauEmissionWithoutProject(bau.getValue());

        // Calculate cumulativeInstalledUnitPerYear
        int previousCumulative = getPreviousCumulativeInstalledUnit(mitigation.getYear());
        int cumulativeInstalledUnitPerYear = previousCumulative + mitigation.getInstalledUnitPerYear();
        mitigation.setCumulativeInstalledUnitPerYear(cumulativeInstalledUnitPerYear);

        // Calculate percentageOfFinalMaximumRate (using solarPVCapacity from mitigation)
        if (mitigation.getSolarPVCapacity() <= 0) {
            throw new RuntimeException("Solar PV Capacity must be greater than 0 for mitigation record");
        }
        int percentageOfFinalMaximumRate = (int) ((cumulativeInstalledUnitPerYear / mitigation.getSolarPVCapacity()) * 100);
        mitigation.setPercentageOfFinalMaximumRate(percentageOfFinalMaximumRate);

        // Calculate avoidedDieselConsumptionAverage from parameter
        double avoidedDieselConsumptionCalculated = (((parameter.getEnergyOutPut() * parameter.getConstant()) / parameter.getDieselEnergyContent()) / (parameter.getGensetEfficiency() / 100.0)  // Convert percentage to decimal
        ) / 1_000_000.0 * (parameter.getPercentageOutPutDisplacedDiesel() / 100);

        parameter.setAvoidedDieselConsumptionCalculated(avoidedDieselConsumptionCalculated);

        // Calculate avoidedDieselConsumptionAverage
        double avoidedDieselConsumptionAverage = (parameter.getAvoidedDieselConsumption() + avoidedDieselConsumptionCalculated) / 2.0;
        // Calculate dieselDisplacedInMillionLitterPerArea
        double dieselDisplacedInMillionLitterPerArea = avoidedDieselConsumptionAverage * (percentageOfFinalMaximumRate / 100.0);
        mitigation.setDieselDisplacedInMillionLitterPerArea(dieselDisplacedInMillionLitterPerArea);

        // Calculate dieselDisplacedInTonJoule
        double dieselDisplacedInTonJoule = dieselDisplacedInMillionLitterPerArea * parameter.getDieselEnergyContent();
        mitigation.setDieselDisplacedInTonJoule(dieselDisplacedInTonJoule);

        // Calculate netGhGMitigationAchieved
        double netGhGMitigationAchieved = (dieselDisplacedInTonJoule * DIESEL_EMISSION_FACTOR) / 1000.0;
        mitigation.setNetGhGMitigationAchieved(netGhGMitigationAchieved);

        // Calculate scenarioGhGEmissionWithProject
        double scenarioGhGEmissionWithProject = mitigation.getBauEmissionWithoutProject() - netGhGMitigationAchieved;
        mitigation.setScenarioGhGEmissionWithProject(scenarioGhGEmissionWithProject);

        // Calculate adjustedBauEmissionMitigation
        double adjustedBauEmissionMitigation = bau.getValue() - netGhGMitigationAchieved;
        mitigation.setAdjustedBauEmissionMitigation(adjustedBauEmissionMitigation);
    }

    private int getPreviousCumulativeInstalledUnit(int year) {
        return mitigationRepository.findFirstByYearLessThanOrderByYearDesc(year)
                .map(RoofTopMitigation::getCumulativeInstalledUnitPerYear)
                .orElse(0);
    }

    private double getCumulativeNetGhGMitigationUpToYear(int upToYear) {
        if (upToYear < 0) {
            return 0.0;
        }

        List<RoofTopMitigation> allMitigations = mitigationRepository.findAllByOrderByYearAsc();
        RoofTopParameter parameter = getLatestParameter();

        double cumulative = 0.0;
        for (RoofTopMitigation m : allMitigations) {
            if (m.getYear() > upToYear) {
                break;
            }

            // Use stored value if available and valid, otherwise calculate
            if (m.getNetGhGMitigationAchieved() > 0) {
                cumulative += m.getNetGhGMitigationAchieved();
            } else {
                // Calculate netGhGMitigation for this record
                if (m.getSolarPVCapacity() <= 0) {
                    // Skip records with invalid solarPVCapacity
                    continue;
                }
                int prevCumulative = getPreviousCumulativeInstalledUnit(m.getYear());
                int cumulativeUnits = prevCumulative + m.getInstalledUnitPerYear();
                int percentage = (int) ((cumulativeUnits / m.getSolarPVCapacity()) * 100);

                double avoidedDieselConsumptionCalculated = (
                        (
                                (parameter.getEnergyOutPut() * parameter.getConstant()) / parameter.getDieselEnergyContent()
                        ) / (parameter.getGensetEfficiency() / 100.0)
                ) / 1_000_000.0
                        * parameter.getPercentageOutPutDisplacedDiesel();

                double avoidedDieselConsumptionAverage = (parameter.getAvoidedDieselConsumption() + avoidedDieselConsumptionCalculated) / 2.0;
                double dieselDisplacedInMillionLitterPerArea = avoidedDieselConsumptionAverage * (percentage / 100.0);
                double dieselDisplacedInTonJoule = dieselDisplacedInMillionLitterPerArea * parameter.getDieselEnergyContent();
                double netGhGMitigation = (dieselDisplacedInTonJoule * DIESEL_EMISSION_FACTOR) / 1000.0;

                cumulative += netGhGMitigation;
            }
        }
        return cumulative;
    }

    private void recalculateSubsequentYears(int fromYear) {
        List<RoofTopMitigation> subsequent = mitigationRepository.findAllByOrderByYearAsc().stream()
                .filter(m -> m.getYear() > fromYear)
                .collect(Collectors.toList());

        RoofTopParameter parameter = getLatestParameter();
        for (RoofTopMitigation mitigation : subsequent) {
            // Get BAU for Energy sector and same year
            Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(mitigation.getYear(), ESector.ENERGY);
            if (bauOptional.isPresent()) {
                BAU bau = bauOptional.get();
                calculateAllFields(mitigation, parameter, bau);
                mitigationRepository.save(mitigation);
            }
        }
    }

    private RoofTopMitigationResponseDto mapEntityToResponseDto(RoofTopMitigation entity) {
        RoofTopMitigationResponseDto dto = new RoofTopMitigationResponseDto();
        dto.setId(entity.getId());
        dto.setYear(entity.getYear());
        dto.setInstalledUnitPerYear(entity.getInstalledUnitPerYear());
        dto.setSolarPVCapacity(entity.getSolarPVCapacity());
        dto.setCumulativeInstalledUnitPerYear(entity.getCumulativeInstalledUnitPerYear());
        dto.setPercentageOfFinalMaximumRate(entity.getPercentageOfFinalMaximumRate());
        dto.setDieselDisplacedInMillionLitterPerArea(entity.getDieselDisplacedInMillionLitterPerArea());
        dto.setDieselDisplacedInTonJoule(entity.getDieselDisplacedInTonJoule());
        dto.setBauEmissionWithoutProject(entity.getBauEmissionWithoutProject());
        dto.setNetGhGMitigationAchieved(entity.getNetGhGMitigationAchieved());
        dto.setScenarioGhGEmissionWithProject(entity.getScenarioGhGEmissionWithProject());
        dto.setAdjustedBauEmissionMitigation(entity.getAdjustedBauEmissionMitigation());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (entity.getProjectIntervention() != null) {
            // Force Hibernate to initialize the proxy while session is still open
            Hibernate.initialize(entity.getProjectIntervention());
            Intervention intervention = entity.getProjectIntervention();
            RoofTopMitigationResponseDto.InterventionInfo interventionInfo =
                    new RoofTopMitigationResponseDto.InterventionInfo(
                            intervention.getId(),
                            intervention.getName()
                    );
            dto.setProjectIntervention(interventionInfo);
        } else {
            dto.setProjectIntervention(null);
        }

        return dto;
    }
}
