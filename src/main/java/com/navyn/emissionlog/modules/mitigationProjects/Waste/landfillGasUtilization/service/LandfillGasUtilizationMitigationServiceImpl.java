package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasUtilizationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasUtilizationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.repository.LandfillGasUtilizationMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class LandfillGasUtilizationMitigationServiceImpl implements LandfillGasUtilizationMitigationService {
    
    private final LandfillGasUtilizationMitigationRepository repository;
    
    @Override
    public LandfillGasUtilizationMitigation createLandfillGasUtilizationMitigation(LandfillGasUtilizationMitigationDto dto) {
        LandfillGasUtilizationMitigation mitigation = new LandfillGasUtilizationMitigation();
        
        // Convert to standard units (ktCO₂eq)
        double bauSolidWasteInKilotonnes = dto.getBauSolidWasteEmissionsUnit().toKilotonnesCO2e(dto.getBauSolidWasteEmissions());
        double projectReductionInKilotonnes = dto.getProjectReductionUnit().toKilotonnesCO2e(dto.getProjectReduction40PercentEfficiency());
        double bauGrandTotalInKilotonnes = dto.getBauGrandTotalUnit().toKilotonnesCO2e(dto.getBauGrandTotal());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setBauSolidWasteEmissions(bauSolidWasteInKilotonnes);
        mitigation.setProjectReduction40PercentEfficiency(projectReductionInKilotonnes);
        mitigation.setBauGrandTotal(bauGrandTotalInKilotonnes);
        
        // Calculations
        // Project Reduction Emissions (KtCO₂eq)
        // if year > 2028: BAU Solid Waste Emissions * Project Reduction (40% Efficiency)
        // else: 0
        Double projectReductionEmissions;
        if (dto.getYear() > 2028) {
            projectReductionEmissions = bauSolidWasteInKilotonnes * projectReductionInKilotonnes;
        } else {
            projectReductionEmissions = 0.0;
        }
        mitigation.setProjectReductionEmissions(projectReductionEmissions);
        
        // Adjusted Solid Waste Emissions (KtCO₂eq)
        // BAU Solid Waste Emissions - Project Reduction Emissions
        Double adjustedSolidWasteEmissions = bauSolidWasteInKilotonnes - projectReductionEmissions;
        mitigation.setAdjustedSolidWasteEmissions(adjustedSolidWasteEmissions);
        
        // Adjusted Grand Total (KtCO₂eq)
        // BAU Grand Total - Project Reduction Emissions
        Double adjustedGrandTotal = bauGrandTotalInKilotonnes - projectReductionEmissions;
        mitigation.setAdjustedGrandTotal(adjustedGrandTotal);
        
        return repository.save(mitigation);
    }
    
    @Override
    public LandfillGasUtilizationMitigation updateLandfillGasUtilizationMitigation(UUID id, LandfillGasUtilizationMitigationDto dto) {
        LandfillGasUtilizationMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Landfill Gas Utilization Mitigation record not found with id: " + id));
        
        // Convert to standard units (ktCO₂eq)
        double bauSolidWasteInKilotonnes = dto.getBauSolidWasteEmissionsUnit().toKilotonnesCO2e(dto.getBauSolidWasteEmissions());
        double projectReductionInKilotonnes = dto.getProjectReductionUnit().toKilotonnesCO2e(dto.getProjectReduction40PercentEfficiency());
        double bauGrandTotalInKilotonnes = dto.getBauGrandTotalUnit().toKilotonnesCO2e(dto.getBauGrandTotal());
        
        // Update user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setBauSolidWasteEmissions(bauSolidWasteInKilotonnes);
        mitigation.setProjectReduction40PercentEfficiency(projectReductionInKilotonnes);
        mitigation.setBauGrandTotal(bauGrandTotalInKilotonnes);
        
        // Recalculate derived fields
        Double projectReductionEmissions;
        if (dto.getYear() > 2028) {
            projectReductionEmissions = bauSolidWasteInKilotonnes * projectReductionInKilotonnes;
        } else {
            projectReductionEmissions = 0.0;
        }
        mitigation.setProjectReductionEmissions(projectReductionEmissions);
        
        Double adjustedSolidWasteEmissions = bauSolidWasteInKilotonnes - projectReductionEmissions;
        mitigation.setAdjustedSolidWasteEmissions(adjustedSolidWasteEmissions);
        
        Double adjustedGrandTotal = bauGrandTotalInKilotonnes - projectReductionEmissions;
        mitigation.setAdjustedGrandTotal(adjustedGrandTotal);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<LandfillGasUtilizationMitigation> getAllLandfillGasUtilizationMitigation(Integer year) {
        Specification<LandfillGasUtilizationMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public void deleteLandfillGasUtilizationMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Landfill Gas Utilization Mitigation record not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
