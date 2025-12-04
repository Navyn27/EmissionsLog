package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.constants.ManureCoveringConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.repository.ManureCoveringMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class ManureCoveringMitigationServiceImpl implements ManureCoveringMitigationService {
    
    private final ManureCoveringMitigationRepository repository;
    
    @Override
    public ManureCoveringMitigation createManureCoveringMitigation(ManureCoveringMitigationDto dto) {
        ManureCoveringMitigation mitigation = new ManureCoveringMitigation();
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());
        
        // Calculations for N2O Reduction (Compaction and Manure Covering)
        // 1. N2O emissions (tonnes CO2e/year) = N2O_EMISSIONS_PER_COW × numberOfCows
        Double n2oEmissions = ManureCoveringConstants.N2O_EMISSIONS_PER_COW.getValue() * dto.getNumberOfCows();
        mitigation.setN2oEmissions(n2oEmissions);
        
        // 2. N2O reduction (30%) = n2oEmissions × N2O_REDUCTION_RATE
        Double n2oReduction = n2oEmissions * ManureCoveringConstants.N2O_REDUCTION_RATE.getValue();
        mitigation.setN2oReduction(n2oReduction);
        
        // 3. Mitigated N2O emissions (ktCO2e/year) = n2oReduction / 1000
        Double mitigatedN2oKilotonnes = n2oReduction / 1000.0;
        mitigation.setMitigatedN2oEmissionsKilotonnes(mitigatedN2oKilotonnes);
        
        return repository.save(mitigation);
    }

    @Override
    public ManureCoveringMitigation updateManureCoveringMitigation(UUID id, ManureCoveringMitigationDto dto) {
        ManureCoveringMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Manure Covering Mitigation record not found with id: " + id));

        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());

        Double n2oEmissions = ManureCoveringConstants.N2O_EMISSIONS_PER_COW.getValue()
            * dto.getNumberOfCows();
        mitigation.setN2oEmissions(n2oEmissions);

        Double n2oReduction = n2oEmissions * ManureCoveringConstants.N2O_REDUCTION_RATE.getValue();
        mitigation.setN2oReduction(n2oReduction);

        Double mitigatedN2oKilotonnes = n2oReduction / 1000.0;
        mitigation.setMitigatedN2oEmissionsKilotonnes(mitigatedN2oKilotonnes);

        return repository.save(mitigation);
    }

    @Override
    public void deleteManureCoveringMitigation(UUID id) {
        ManureCoveringMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Manure Covering Mitigation record not found with id: " + id));
        repository.delete(mitigation);
    }
    
    @Override
    public List<ManureCoveringMitigation> getAllManureCoveringMitigation(Integer year) {
        Specification<ManureCoveringMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
