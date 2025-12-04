package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository.ISWMMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class ISWMMitigationServiceImpl implements ISWMMitigationService {
    
    private final ISWMMitigationRepository repository;
    
    @Override
    public ISWMMitigation createISWMMitigation(ISWMMitigationDto dto) {
        ISWMMitigation mitigation = new ISWMMitigation();
        
        // Convert to standard units (tCO₂e)
        double bauEmissionsInTonnes = dto.getBauEmissionsUnit().toTonnesCO2e(dto.getBauEmissions());
        double annualReductionInTonnes = dto.getAnnualReductionUnit().toTonnesCO2e(dto.getAnnualReduction());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setBauEmissions(bauEmissionsInTonnes);
        mitigation.setAnnualReduction(annualReductionInTonnes);
        
        // Calculation
        // Adjusted Emissions (tCO₂e) = BAU Emissions (tCO₂e) - Annual Reduction (tCO₂e)
        Double adjustedEmissions = bauEmissionsInTonnes - annualReductionInTonnes;
        mitigation.setAdjustedEmissions(adjustedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public ISWMMitigation updateISWMMitigation(UUID id, ISWMMitigationDto dto) {
        ISWMMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("ISWM Mitigation record not found with id: " + id));
        
        // Convert to standard units (tCO₂e)
        double bauEmissionsInTonnes = dto.getBauEmissionsUnit().toTonnesCO2e(dto.getBauEmissions());
        double annualReductionInTonnes = dto.getAnnualReductionUnit().toTonnesCO2e(dto.getAnnualReduction());
        
        // Update user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setBauEmissions(bauEmissionsInTonnes);
        mitigation.setAnnualReduction(annualReductionInTonnes);
        
        // Recalculate derived field
        Double adjustedEmissions = bauEmissionsInTonnes - annualReductionInTonnes;
        mitigation.setAdjustedEmissions(adjustedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<ISWMMitigation> getAllISWMMitigation(Integer year) {
        Specification<ISWMMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
