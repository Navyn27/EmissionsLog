package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model.RoofTopMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model.RoofTopParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.repository.IRoofTopMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.repository.IRoofTopParameterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoofTopServiceImpl implements IRoofTopMitigationService {

    private final IRoofTopMitigationRepository mitigationRepository;
    private final IRoofTopParameterRepository parameterRepository;
    private final IRoofTopParameterService parameterService;

    private static final double DIESEL_EMISSION_FACTOR = 74.1;

    @Override
    @Transactional
    public RoofTopMitigationResponseDto create(RoofTopMitigationDto dto) {
        // Check if year already exists
        if (mitigationRepository.findByYear(dto.getYear()).isPresent()) {
            throw new RuntimeException("Mitigation record for year " + dto.getYear() + " already exists");
        }

        // Get latest parameters
        RoofTopParameter parameter = getLatestParameter();

        RoofTopMitigation mitigation = new RoofTopMitigation();
        mitigation.setYear(dto.getYear());
        mitigation.setInstalledUnitPerYear(dto.getInstalledUnitPerYear());
        mitigation.setBauEmissionWithoutProject(dto.getBauEmissionWithoutProject());

        // Calculate all fields
        calculateAllFields(mitigation, parameter);

        RoofTopMitigation saved = mitigationRepository.save(mitigation);
        return mapEntityToResponseDto(saved);
    }

    @Override
    public RoofTopMitigationResponseDto getById(UUID id) {
        RoofTopMitigation mitigation = mitigationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RoofTopMitigation not found with id: " + id));

        RoofTopParameter parameter = getLatestParameter();
        calculateAllFields(mitigation, parameter);

        return mapEntityToResponseDto(mitigation);
    }

    @Override
    public List<RoofTopMitigationResponseDto> getAll() {
        RoofTopParameter parameter = getLatestParameter();

        return mitigationRepository.findAllByOrderByYearAsc().stream()
                .map(mitigation -> {
                    calculateAllFields(mitigation, parameter);
                    return mapEntityToResponseDto(mitigation);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoofTopMitigationResponseDto update(UUID id, RoofTopMitigationDto dto) {
        RoofTopMitigation mitigation = mitigationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RoofTopMitigation not found with id: " + id));

        // Check if year is being changed and if new year already exists
        if (mitigation.getYear() != dto.getYear()) {
            if (mitigationRepository.findByYear(dto.getYear()).isPresent()) {
                throw new EntityNotFoundException("Mitigation record for year " + dto.getYear() + " already exists");
            }
        }

        mitigation.setYear(dto.getYear());
        mitigation.setInstalledUnitPerYear(dto.getInstalledUnitPerYear());
        mitigation.setBauEmissionWithoutProject(dto.getBauEmissionWithoutProject());

        RoofTopParameter parameter = getLatestParameter();
        calculateAllFields(mitigation, parameter);

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
    public RoofTopMitigationResponseDto getByYear(int year) {
        RoofTopMitigation mitigation = mitigationRepository.findByYear(year)
                .orElseThrow(() -> new EntityNotFoundException("RoofTopMitigation not found for year: " + year));

        RoofTopParameter parameter = getLatestParameter();
        calculateAllFields(mitigation, parameter);

        return mapEntityToResponseDto(mitigation);
    }

    private RoofTopParameter getLatestParameter() {
        List<RoofTopParameter> all = parameterRepository.findAll();
        if (all.isEmpty()) {
            throw new EntityNotFoundException("No RoofTopParameter found. Please create parameters first.");
        }
        return all.stream()
                .max((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt()))
                .orElseThrow(() -> new EntityNotFoundException("No RoofTopParameter found"));
    }

    private void calculateAllFields(RoofTopMitigation mitigation, RoofTopParameter parameter) {
        // Calculate cumulativeInstalledUnitPerYear
        int previousCumulative = getPreviousCumulativeInstalledUnit(mitigation.getYear());
        int cumulativeInstalledUnitPerYear = previousCumulative + mitigation.getInstalledUnitPerYear();
        mitigation.setCumulativeInstalledUnitPerYear(cumulativeInstalledUnitPerYear);

        // Calculate percentageOfFinalMaximumRate
        int percentageOfFinalMaximumRate = (int) ((cumulativeInstalledUnitPerYear / parameter.getSolarPVCapacity()) * 100);
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
                int prevCumulative = getPreviousCumulativeInstalledUnit(m.getYear());
                int cumulativeUnits = prevCumulative + m.getInstalledUnitPerYear();
                int percentage = (int) ((cumulativeUnits / parameter.getSolarPVCapacity()) * 100);

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
            calculateAllFields(mitigation, parameter);
            mitigationRepository.save(mitigation);
        }
    }

    private RoofTopMitigationResponseDto mapEntityToResponseDto(RoofTopMitigation entity) {
        RoofTopMitigationResponseDto dto = new RoofTopMitigationResponseDto();
        dto.setId(entity.getId());
        dto.setYear(entity.getYear());
        dto.setInstalledUnitPerYear(entity.getInstalledUnitPerYear());
        dto.setCumulativeInstalledUnitPerYear(entity.getCumulativeInstalledUnitPerYear());
        dto.setPercentageOfFinalMaximumRate(entity.getPercentageOfFinalMaximumRate());
        dto.setDieselDisplacedInMillionLitterPerArea(entity.getDieselDisplacedInMillionLitterPerArea());
        dto.setDieselDisplacedInTonJoule(entity.getDieselDisplacedInTonJoule());
        dto.setBauEmissionWithoutProject(entity.getBauEmissionWithoutProject());
        dto.setNetGhGMitigationAchieved(entity.getNetGhGMitigationAchieved());
        dto.setScenarioGhGEmissionWithProject(entity.getScenarioGhGEmissionWithProject());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

//    private double roundValue(double value) {
//        return (double) Math.round(value);
//    }
}
