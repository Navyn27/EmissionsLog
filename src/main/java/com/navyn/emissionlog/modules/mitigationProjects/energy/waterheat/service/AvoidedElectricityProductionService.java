package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto.AvoidedElectricityProductionDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.AvoidedElectricityProduction;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.WaterHeatParameter;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository.AvoidedElectricityProductionRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository.WaterHeatParameterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public AvoidedElectricityProduction createFromDTO(AvoidedElectricityProductionDTO dto) {
        return calculateAndSave(new AvoidedElectricityProduction(), dto);
    }

    @Transactional
    public AvoidedElectricityProduction updateFromDTO(UUID id, AvoidedElectricityProductionDTO dto) {
        AvoidedElectricityProduction existing = getById(id);
        return calculateAndSave(existing, dto);
    }

    private AvoidedElectricityProduction calculateAndSave(AvoidedElectricityProduction aep, AvoidedElectricityProductionDTO dto) {
        WaterHeatParameter param = paramRepository.findById(dto.getWaterHeatParameterId())
                .orElseThrow(() -> new RuntimeException("WaterHeatParameter not found with id " + dto.getWaterHeatParameterId()));

        int lastCumulative = repository.findAll().stream()
                .filter(record -> !record.getId().equals(aep.getId())) // Exclude current record from calculation
                .mapToInt(AvoidedElectricityProduction::getCumulativeUnitsInstalled)
                .max()
                .orElse(0);

        int cumulativeUnits = lastCumulative + dto.getUnitsInstalledThisYear();

        aep.setYear(dto.getYear());
        aep.setUnitsInstalledThisYear(dto.getUnitsInstalledThisYear());
        aep.setCumulativeUnitsInstalled(cumulativeUnits);
        aep.setAnnualAvoidedElectricity(dto.getUnitsInstalledThisYear() * param.getAvoidedElectricityPerHousehold());
        aep.setCumulativeAvoidedElectricity(cumulativeUnits * param.getAvoidedElectricityPerHousehold());

        Double factor = emissionFactorService.getFactor(dto.getYear());

        if (factor != null) {
            double cumAvoided = aep.getCumulativeAvoidedElectricity();
            double denominator = (0.9 * 1000) - (0.003 * cumAvoided);

            if (denominator <= 0) {
                throw new RuntimeException("Invalid denominator in net GHG mitigation formula for year " + dto.getYear());
            }

            double netGHG = (cumAvoided * factor) / denominator;
            aep.setNetGhGMitigation(netGHG);
        } else {
            aep.setNetGhGMitigation(null);
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

    public List<AvoidedElectricityProduction> getByYear(int year) {
        return  repository.findAllByYear(year);
    }
}
