package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.service;

import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.services.BAUService;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto.AvoidedElectricityProductionDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto.AvoidedElectricityProductionResponseDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.AvoidedElectricityProduction;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.WaterHeatParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository.AvoidedElectricityProductionRepository;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AvoidedElectricityProductionService {

    private final AvoidedElectricityProductionRepository repository;
    private final WaterHeatParameterService parameterService;
    private final InterventionRepository interventionRepository;
    private final BAUService bauService;

    public AvoidedElectricityProductionService(
            AvoidedElectricityProductionRepository repository,
            WaterHeatParameterService parameterService,
            InterventionRepository interventionRepository,
            BAUService bauService) {
        this.repository = repository;
        this.parameterService = parameterService;
        this.interventionRepository = interventionRepository;
        this.bauService = bauService;
    }

    /**
     * Maps AvoidedElectricityProduction entity to Response DTO
     * This method loads intervention data within the transaction to avoid lazy loading issues
     */
    @Transactional
    private AvoidedElectricityProductionResponseDTO toResponseDto(AvoidedElectricityProduction aep) {
        AvoidedElectricityProductionResponseDTO dto = new AvoidedElectricityProductionResponseDTO();
        dto.setId(aep.getId());
        dto.setYear(aep.getYear());
        dto.setUnitsInstalledThisYear(aep.getUnitsInstalledThisYear());
        dto.setCumulativeUnitsInstalled(aep.getCumulativeUnitsInstalled());
        dto.setAverageWaterHeat(aep.getAverageWaterHeat());
        dto.setAnnualAvoidedElectricity(aep.getAnnualAvoidedElectricity());
        dto.setCumulativeAvoidedElectricity(aep.getCumulativeAvoidedElectricity());
        dto.setNetGhGMitigation(aep.getNetGhGMitigation());
        dto.setAdjustedBauEmissionMitigation(aep.getAdjustedBauEmissionMitigation());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (aep.getProjectIntervention() != null) {
            // Force Hibernate to initialize the proxy while session is still open
            Hibernate.initialize(aep.getProjectIntervention());
            Intervention intervention = aep.getProjectIntervention();
            AvoidedElectricityProductionResponseDTO.InterventionInfo interventionInfo =
                    new AvoidedElectricityProductionResponseDTO.InterventionInfo(
                            intervention.getId(),
                            intervention.getName()
                    );
            dto.setProjectIntervention(interventionInfo);
        } else {
            dto.setProjectIntervention(null);
        }

        return dto;
    }

    // CREATE using DTO - returns entity (for internal use)
    public AvoidedElectricityProduction createFromDTO(AvoidedElectricityProductionDTO dto) {

        // 1️⃣ Get latest active WaterHeatParameter (automatically detected)
        WaterHeatParameter param = parameterService.getLatestActiveEntity();

        // 2️⃣ Fetch Intervention (if provided)
        Intervention intervention = null;
        if (dto.getProjectInterventionId() != null) {
            intervention = interventionRepository.findById(dto.getProjectInterventionId())
                    .orElseThrow(() -> new RuntimeException("Intervention not found with id " + dto.getProjectInterventionId()));
        }

        // 3️⃣ Get BAU for Energy sector and same year
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(dto.getYear(), ESector.ENERGY);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + dto.getYear() + " and sector ENERGY. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();

        // 4️⃣ Calculate cumulativeUnitsInstalled
        int lastCumulative = repository.findAll().stream()
                .mapToInt(AvoidedElectricityProduction::getCumulativeUnitsInstalled)
                .max()
                .orElse(0);

        int cumulativeUnits = lastCumulative + dto.getUnitsInstalledThisYear();

        // 5️⃣ Create entity (calculates annual & cumulative avoided electricity)
        AvoidedElectricityProduction aep = new AvoidedElectricityProduction(
                dto.getYear(),
                dto.getUnitsInstalledThisYear(),
                cumulativeUnits,
                dto.getAverageWaterHeat(),
                param
        );
        aep.setProjectIntervention(intervention);

        // 6️⃣ Get grid emission factor from WaterHeatParameter (dynamic, not constant)
        Double factor = param.getGridEmissionFactor();

        if (factor == null) {
            throw new RuntimeException("Grid emission factor is not set for WaterHeatParameter. Please update the parameter with a grid emission factor.");
        }
        
        double cumAvoided = aep.getCumulativeAvoidedElectricity(); // MWh

        // 7️⃣ APPLY CORRECT FORMULA for net GHG mitigation
        double denominator = (0.9 * 1000) - (0.003 * cumAvoided);

        if (denominator <= 0) {
            throw new RuntimeException("Invalid denominator in net GHG mitigation formula for year " + dto.getYear());
        }

        double netGHG = (cumAvoided * factor) / denominator; // tCO2
        aep.setNetGhGMitigation(netGHG);

        // 8️⃣ Calculate adjustedBauEmissionMitigation (ktCO2e) = BAU (ktCO2e) - netGhGMitigation (tCO2) / 1000
        double netGhGMitigationKilotonnes = netGHG / 1000.0; // Convert tCO2 to ktCO2e
        double adjustedBauEmissionMitigation = bau.getValue() - netGhGMitigationKilotonnes;
        aep.setAdjustedBauEmissionMitigation(adjustedBauEmissionMitigation);

        return repository.save(aep);
    }

    @Transactional
    public AvoidedElectricityProductionResponseDTO create(AvoidedElectricityProductionDTO dto) {
        AvoidedElectricityProduction saved = createFromDTO(dto);
        return toResponseDto(saved);
    }

    @Transactional
    public List<AvoidedElectricityProductionResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AvoidedElectricityProductionResponseDTO getById(UUID id) {
        AvoidedElectricityProduction aep = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AvoidedElectricityProduction not found with id " + id));
        return toResponseDto(aep);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    // UPDATE using DTO - returns entity (for internal use)
    public AvoidedElectricityProduction updateFromDTO(UUID id, @Valid AvoidedElectricityProductionDTO dto) {
        // Fetch existing record
        AvoidedElectricityProduction existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AvoidedElectricityProduction not found with id " + id));

        // Get latest active WaterHeatParameter (automatically detected)
        WaterHeatParameter param = parameterService.getLatestActiveEntity();

        // Fetch Intervention (if provided)
        Intervention intervention = null;
        if (dto.getProjectInterventionId() != null) {
            intervention = interventionRepository.findById(dto.getProjectInterventionId())
                    .orElseThrow(() -> new RuntimeException("Intervention not found with id " + dto.getProjectInterventionId()));
        }

        // Get BAU for Energy sector and same year
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(dto.getYear(), ESector.ENERGY);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + dto.getYear() + " and sector ENERGY. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();

        // Recalculate cumulativeUnitsInstalled
        int lastCumulative = repository.findAll().stream()
                .filter(aep -> !aep.getId().equals(id)) // Exclude current record from calculation
                .mapToInt(AvoidedElectricityProduction::getCumulativeUnitsInstalled)
                .max()
                .orElse(0);

        int cumulativeUnits = lastCumulative + dto.getUnitsInstalledThisYear();

        // Update fields
        existing.setYear(dto.getYear());
        existing.setUnitsInstalledThisYear(dto.getUnitsInstalledThisYear());
        existing.setCumulativeUnitsInstalled(cumulativeUnits);
        existing.setAverageWaterHeat(dto.getAverageWaterHeat());
        existing.setProjectIntervention(intervention);

        // Recalculate avoided electricity values using averageWaterHeat from DTO
        double avoidedElectricityPerHousehold = param.getAvoidedElectricityPerHousehold(dto.getAverageWaterHeat());
        existing.setAnnualAvoidedElectricity(
                dto.getUnitsInstalledThisYear() * avoidedElectricityPerHousehold
        );

        existing.setCumulativeAvoidedElectricity(
                cumulativeUnits * avoidedElectricityPerHousehold
        );

        // Recalculate net GHG mitigation using gridEmissionFactor from WaterHeatParameter
        Double factor = param.getGridEmissionFactor();

        if (factor == null) {
            throw new RuntimeException("Grid emission factor is not set for WaterHeatParameter. Please update the parameter with a grid emission factor.");
        }
        
        double cumAvoided = existing.getCumulativeAvoidedElectricity(); // MWh

        // Apply correct formula
        double denominator = (0.9 * 1000) - (0.003 * cumAvoided);

        if (denominator <= 0) {
            throw new RuntimeException("Invalid denominator in net GHG mitigation formula for year " + dto.getYear());
        }

        double netGHG = (cumAvoided * factor) / denominator; // tCO2
        existing.setNetGhGMitigation(netGHG);

        // Calculate adjustedBauEmissionMitigation (ktCO2e) = BAU (ktCO2e) - netGhGMitigation (tCO2) / 1000
        double netGhGMitigationKilotonnes = netGHG / 1000.0; // Convert tCO2 to ktCO2e
        double adjustedBauEmissionMitigation = bau.getValue() - netGhGMitigationKilotonnes;
        existing.setAdjustedBauEmissionMitigation(adjustedBauEmissionMitigation);

        return repository.save(existing);
    }

    @Transactional
    public AvoidedElectricityProductionResponseDTO update(UUID id, @Valid AvoidedElectricityProductionDTO dto) {
        AvoidedElectricityProduction saved = updateFromDTO(id, dto);
        return toResponseDto(saved);
    }

    @Transactional
    public List<AvoidedElectricityProductionResponseDTO> getByYear(int year) {
        return repository.findByYear(year).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}
