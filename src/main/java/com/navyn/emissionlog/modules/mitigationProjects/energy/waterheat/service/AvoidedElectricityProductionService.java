package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto.AvoidedElectricityProductionDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.AvoidedElectricityProduction;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.WaterHeatParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository.AvoidedElectricityProductionRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository.WaterHeatParameterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AvoidedElectricityProductionService {

    private final AvoidedElectricityProductionRepository repository;
    private final WaterHeatParameterRepository paramRepository;
    private final GridEmissionFactorService emissionFactorService;

    public AvoidedElectricityProductionService(
            AvoidedElectricityProductionRepository repository,
            WaterHeatParameterRepository paramRepository,
            GridEmissionFactorService emissionFactorService) {
        this.repository = repository;
        this.paramRepository = paramRepository;
        this.emissionFactorService = emissionFactorService;
    }

    // CREATE using DTO
    public AvoidedElectricityProduction createFromDTO(AvoidedElectricityProductionDTO dto) {

        // 1️⃣ Fetch WaterHeatParameter
        WaterHeatParameter param = paramRepository.findById(dto.getWaterHeatParameterId())
                .orElseThrow(() -> new RuntimeException("WaterHeatParameter not found with id " + dto.getWaterHeatParameterId()));

        // 2️⃣ Calculate cumulativeUnitsInstalled
        int lastCumulative = repository.findAll().stream()
                .mapToInt(AvoidedElectricityProduction::getCumulativeUnitsInstalled)
                .max()
                .orElse(0);

        int cumulativeUnits = lastCumulative + dto.getUnitsInstalledThisYear();

        // 3️⃣ Create entity (calculates annual & cumulative avoided electricity)
        AvoidedElectricityProduction aep = new AvoidedElectricityProduction(
                dto.getYear(),
                dto.getUnitsInstalledThisYear(),
                cumulativeUnits,
                param
        );

        // 4️⃣ Get grid emission factor
        Double factor = emissionFactorService.getFactor(dto.getYear());

        if (factor != null) {
            double cumAvoided = aep.getCumulativeAvoidedElectricity(); // MWh

            // 5️⃣ APPLY CORRECT FORMULA
            double denominator = (0.9 * 1000) - (0.003 * cumAvoided);

            if (denominator <= 0) {
                throw new RuntimeException("Invalid denominator in net GHG mitigation formula for year " + dto.getYear());
            }

            double netGHG = (cumAvoided * factor) / denominator;

            aep.setNetGhGMitigation(netGHG);
        }

        return repository.save(aep);
    }

    public List<AvoidedElectricityProduction> getAll() {
        return repository.findAll();
    }

    public AvoidedElectricityProduction getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AvoidedElectricityProduction not found with id " + id));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
