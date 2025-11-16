package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository.ISWMMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class ISWMMitigationServiceImpl implements ISWMMitigationService {
    
    private final ISWMMitigationRepository repository;
    
    @Override
    public ISWMMitigation createISWMMitigation(ISWMMitigationDto dto) {
        ISWMMitigation mitigation = new ISWMMitigation();
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setBauEmissions(dto.getBauEmissions());
        mitigation.setAnnualReduction(dto.getAnnualReduction());
        
        // Calculation
        // Adjusted Emissions (ktCO2e) = BAU Emissions (ktCO2e) - Annual Reduction (ktCO2e)
        Double adjustedEmissions = dto.getBauEmissions() - dto.getAnnualReduction();
        mitigation.setAdjustedEmissions(adjustedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<ISWMMitigation> getAllISWMMitigation(Integer year) {
        Specification<ISWMMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
