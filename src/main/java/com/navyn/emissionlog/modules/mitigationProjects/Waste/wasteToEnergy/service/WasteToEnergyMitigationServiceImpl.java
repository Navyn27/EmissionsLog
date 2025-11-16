package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.constants.WasteToEnergyConstants;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToEnergyMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToEnergyMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.repository.WasteToEnergyMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class WasteToEnergyMitigationServiceImpl implements WasteToEnergyMitigationService {
    
    private final WasteToEnergyMitigationRepository repository;
    
    @Override
    public WasteToEnergyMitigation createWasteToEnergyMitigation(WasteToEnergyMitigationDto dto) {
        WasteToEnergyMitigation mitigation = new WasteToEnergyMitigation();
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setWasteToWtE(dto.getWasteToWtE());
        mitigation.setBauEmissionsSolidWaste(dto.getBauEmissionsSolidWaste());
        
        // Calculations
        // GHG Reduction (tCO2eq) = Net Emission Factor (tCO2eq/t) * Waste to WtE (t/year)
        Double ghgReductionTonnes = WasteToEnergyConstants.NET_EMISSION_FACTOR.getValue() * dto.getWasteToWtE();
        mitigation.setGhgReductionTonnes(ghgReductionTonnes);
        
        // GHG Reduction (KtCO2eq) = GHG Reduction (tCO2eq) / 1000
        Double ghgReductionKilotonnes = ghgReductionTonnes / 1000;
        mitigation.setGhgReductionKilotonnes(ghgReductionKilotonnes);
        
        // Adjusted Emissions (with WtE, ktCO₂e) = BAU Emissions (Solid Waste, ktCO₂e) - GHG Reduction (KtCO2eq)
        Double adjustedEmissions = dto.getBauEmissionsSolidWaste() - ghgReductionKilotonnes;
        mitigation.setAdjustedEmissionsWithWtE(adjustedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<WasteToEnergyMitigation> getAllWasteToEnergyMitigation(Integer year) {
        Specification<WasteToEnergyMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
