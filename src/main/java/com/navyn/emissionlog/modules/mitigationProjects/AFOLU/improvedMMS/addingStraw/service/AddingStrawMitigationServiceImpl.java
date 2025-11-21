package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.constants.AddingStrawConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.repository.AddingStrawMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class AddingStrawMitigationServiceImpl implements AddingStrawMitigationService {
    
    private final AddingStrawMitigationRepository repository;
    
    @Override
    public AddingStrawMitigation createAddingStrawMitigation(AddingStrawMitigationDto dto) {
        AddingStrawMitigation mitigation = new AddingStrawMitigation();
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());
        
        // Calculations for CH4 Reduction (Adding Straw)
        // 1. CH4 emissions if no mitigation (tonnes CO2e) = CH4_EMISSIONS_PER_COW × numberOfCows
        Double ch4EmissionsStraw = AddingStrawConstants.CH4_EMISSIONS_PER_COW_ADDING_STRAW.getValue() * dto.getNumberOfCows();
        mitigation.setCh4EmissionsAddingStraw(ch4EmissionsStraw);
        
        // 2. CH4 reduction (30%) = ch4EmissionsStraw × CH4_REDUCTION_RATE_STRAW
        Double ch4ReductionStraw = ch4EmissionsStraw * AddingStrawConstants.CH4_REDUCTION_RATE_STRAW.getValue();
        mitigation.setCh4ReductionAddingStraw(ch4ReductionStraw);
        
        // 3. Mitigated CH4 emissions (ktCO2e/year) = ch4ReductionStraw / 1000
        Double mitigatedCh4Kilotonnes = ch4ReductionStraw / 1000.0;
        mitigation.setMitigatedCh4EmissionsKilotonnes(mitigatedCh4Kilotonnes);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<AddingStrawMitigation> getAllAddingStrawMitigation(Integer year) {
        Specification<AddingStrawMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
