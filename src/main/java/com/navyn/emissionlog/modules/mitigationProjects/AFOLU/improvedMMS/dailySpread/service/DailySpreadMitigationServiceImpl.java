package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.constants.DailySpreadConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.repository.DailySpreadMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class DailySpreadMitigationServiceImpl implements DailySpreadMitigationService {
    
    private final DailySpreadMitigationRepository repository;
    
    @Override
    public DailySpreadMitigation createDailySpreadMitigation(DailySpreadMitigationDto dto) {
        DailySpreadMitigation mitigation = new DailySpreadMitigation();
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());
        
        // Calculations for CH4 Reduction (Daily Spread MMS)
        // 1. CH4 emissions per cow (tonnes CO2e/year) = CH4_EMISSIONS_PER_COW × numberOfCows
        Double ch4EmissionsDailySpread = DailySpreadConstants.CH4_EMISSIONS_PER_COW_DAILY_SPREAD.getValue() * dto.getNumberOfCows();
        mitigation.setCh4EmissionsDailySpread(ch4EmissionsDailySpread);
        
        // 2. CH4 reduction (50%) = ch4EmissionsDailySpread × CH4_REDUCTION_RATE_DAILY_SPREAD
        Double ch4ReductionDailySpread = ch4EmissionsDailySpread * DailySpreadConstants.CH4_REDUCTION_RATE_DAILY_SPREAD.getValue();
        mitigation.setCh4ReductionDailySpread(ch4ReductionDailySpread);
        
        // 3. Mitigated CH4 emissions (ktCO2e/year) = ch4ReductionDailySpread / 1000
        Double mitigatedCh4Kilotonnes = ch4ReductionDailySpread / 1000.0;
        mitigation.setMitigatedCh4EmissionsKilotonnes(mitigatedCh4Kilotonnes);
        
        return repository.save(mitigation);
    }

    @Override
    public DailySpreadMitigation updateDailySpreadMitigation(UUID id, DailySpreadMitigationDto dto) {
        DailySpreadMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Daily Spread Mitigation record not found with id: " + id));

        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());

        Double ch4EmissionsDailySpread = DailySpreadConstants.CH4_EMISSIONS_PER_COW_DAILY_SPREAD.getValue()
            * dto.getNumberOfCows();
        mitigation.setCh4EmissionsDailySpread(ch4EmissionsDailySpread);

        Double ch4ReductionDailySpread = ch4EmissionsDailySpread
            * DailySpreadConstants.CH4_REDUCTION_RATE_DAILY_SPREAD.getValue();
        mitigation.setCh4ReductionDailySpread(ch4ReductionDailySpread);

        Double mitigatedCh4Kilotonnes = ch4ReductionDailySpread / 1000.0;
        mitigation.setMitigatedCh4EmissionsKilotonnes(mitigatedCh4Kilotonnes);

        return repository.save(mitigation);
    }

    @Override
    public void deleteDailySpreadMitigation(UUID id) {
        DailySpreadMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Daily Spread Mitigation record not found with id: " + id));
        repository.delete(mitigation);
    }
    
    @Override
    public List<DailySpreadMitigation> getAllDailySpreadMitigation(Integer year) {
        Specification<DailySpreadMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
