package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasUtilizationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasUtilizationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.repository.LandfillGasUtilizationMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class LandfillGasUtilizationMitigationServiceImpl implements LandfillGasUtilizationMitigationService {
    
    private final LandfillGasUtilizationMitigationRepository repository;
    
    @Override
    public LandfillGasUtilizationMitigation createLandfillGasUtilizationMitigation(LandfillGasUtilizationMitigationDto dto) {
        LandfillGasUtilizationMitigation mitigation = new LandfillGasUtilizationMitigation();
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setBauSolidWasteEmissions(dto.getBauSolidWasteEmissions());
        mitigation.setProjectReduction40PercentEfficiency(dto.getProjectReduction40PercentEfficiency());
        mitigation.setBauGrandTotal(dto.getBauGrandTotal());
        
        // Calculations
        // Project Reduction Emissions (KtCO₂eq)
        // if year > 2028: BAU Solid Waste Emissions * Project Reduction (40% Efficiency)
        // else: 0
        Double projectReductionEmissions;
        if (dto.getYear() > 2028) {
            projectReductionEmissions = dto.getBauSolidWasteEmissions() * dto.getProjectReduction40PercentEfficiency();
        } else {
            projectReductionEmissions = 0.0;
        }
        mitigation.setProjectReductionEmissions(projectReductionEmissions);
        
        // Adjusted Solid Waste Emissions (KtCO₂eq)
        // BAU Solid Waste Emissions - Project Reduction Emissions
        Double adjustedSolidWasteEmissions = dto.getBauSolidWasteEmissions() - projectReductionEmissions;
        mitigation.setAdjustedSolidWasteEmissions(adjustedSolidWasteEmissions);
        
        // Adjusted Grand Total (KtCO₂eq)
        // BAU Grand Total - Project Reduction Emissions
        Double adjustedGrandTotal = dto.getBauGrandTotal() - projectReductionEmissions;
        mitigation.setAdjustedGrandTotal(adjustedGrandTotal);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<LandfillGasUtilizationMitigation> getAllLandfillGasUtilizationMitigation(Integer year) {
        Specification<LandfillGasUtilizationMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
