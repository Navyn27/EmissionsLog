package com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.service;

import com.navyn.emissionlog.Enums.Mitigation.ImprovedMMSConstants;
import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.dtos.ImprovedMMSMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.models.ImprovedMMSMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.repositories.ImprovedMMSMitigationRepository;
import com.navyn.emissionlog.utils.Specifications.MitigationSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImprovedMMSMitigationServiceImpl implements ImprovedMMSMitigationService {
    
    private final ImprovedMMSMitigationRepository repository;
    
    @Override
    public ImprovedMMSMitigation createImprovedMMSMitigation(ImprovedMMSMitigationDto dto) {
        ImprovedMMSMitigation mitigation = new ImprovedMMSMitigation();
        
        // Map input fields
        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());
        
        // === STRATEGY 1: N2O REDUCTION (MANURE COVERING) ===
        // 1. N2O emissions (tonnes CO2e/year)
        double n2oEmissions = ImprovedMMSConstants.N2O_EMISSIONS_PER_COW.getValue() * dto.getNumberOfCows();
        mitigation.setN2oEmissions(n2oEmissions);
        
        // 2. N2O reduction (30%) with manure plastic covering
        double n2oReduction = n2oEmissions * ImprovedMMSConstants.N2O_REDUCTION_RATE.getValue();
        mitigation.setN2oReduction(n2oReduction);
        
        // 3. Mitigated N2O emissions (Kt CO2e/year)
        double mitigatedN2o = n2oReduction / 1000.0;
        mitigation.setMitigatedN2oEmissions(mitigatedN2o);
        
        // === STRATEGY 2: CH4 REDUCTION (ADDING STRAW) ===
        // 4. CH4 emissions if no mitigation (tonnes CO2e)
        double ch4EmissionsStraw = ImprovedMMSConstants.CH4_EMISSIONS_PER_COW_ADDING_STRAW.getValue() * 
            dto.getNumberOfCows();
        mitigation.setCh4EmissionsAddingStraw(ch4EmissionsStraw);
        
        // 5. CH4 reduction (30%) with straw addition
        double ch4ReductionStraw = ch4EmissionsStraw * 
            ImprovedMMSConstants.CH4_REDUCTION_RATE_STRAW.getValue();
        mitigation.setCh4ReductionAddingStraw(ch4ReductionStraw);
        
        // 6. Mitigated CH4 emissions (Kt CO2e/year)
        double mitigatedCh4Straw = ch4ReductionStraw / 1000.0;
        mitigation.setMitigatedCh4EmissionsAddingStraw(mitigatedCh4Straw);
        
        // === STRATEGY 3: CH4 REDUCTION (DAILY SPREAD MMS) ===
        // 7. CH4 emissions per cow (tonnes CO2e/year)
        double ch4EmissionsDailySpread = ImprovedMMSConstants.CH4_EMISSIONS_PER_COW_DAILY_SPREAD.getValue() * 
            dto.getNumberOfCows();
        mitigation.setCh4EmissionsDailySpread(ch4EmissionsDailySpread);
        
        // 8. CH4 reduction (50%) with daily spread MMS
        double ch4ReductionDailySpread = ch4EmissionsDailySpread * 
            ImprovedMMSConstants.CH4_REDUCTION_RATE_DAILY_SPREAD.getValue();
        mitigation.setCh4ReductionDailySpread(ch4ReductionDailySpread);
        
        // 9. Mitigated CH4 emissions (Kt CO2e/year)
        double mitigatedCh4DailySpread = ch4ReductionDailySpread / 1000.0;
        mitigation.setMitigatedCh4EmissionsDailySpread(mitigatedCh4DailySpread);
        
        // === TOTAL MITIGATION ===
        double totalMitigation = mitigatedN2o + mitigatedCh4Straw + mitigatedCh4DailySpread;
        mitigation.setTotalMitigation(totalMitigation);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<ImprovedMMSMitigation> getAllImprovedMMSMitigation(Integer year) {
        Specification<ImprovedMMSMitigation> spec = 
            Specification.<ImprovedMMSMitigation>where(MitigationSpecifications.hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public Optional<ImprovedMMSMitigation> getByYear(Integer year) {
        return repository.findByYear(year);
    }
}
