package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigationYear;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveMitigationYearRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoveMitigationService {

    private final StoveMitigationYearRepository mitigationRepository;

    public StoveMitigationService(StoveMitigationYearRepository mitigationRepository) {
        this.mitigationRepository = mitigationRepository;
    }

    public List<StoveMitigationYear> calculateMitigation(
            StoveType stoveType,
            List<Integer> unitsInstalledPerYear,
            int startYear
    ) {
        List<StoveMitigationYear> results = new ArrayList<>();

        double baselinePercentage = stoveType.getBaselinePercentage(); // e.g., 0.40
        double conversionFactor = 0.029;
        double stoveFactor = 4 * 112 * 0.647;
        double assumptionsC11 = 8.43; // tCO2e/TJ
        double scale = 1e-3;

        for (int i = 0; i < unitsInstalledPerYear.size(); i++) {
            int year = startYear + i;
            int unitsThisYear = unitsInstalledPerYear.get(i);

            // Calculate avoided emissions per your formula
            double avoidedEmissions = baselinePercentage * unitsThisYear
                    * conversionFactor * (stoveFactor + assumptionsC11) * scale;

            // Create entity
            StoveMitigationYear mitigation = new StoveMitigationYear();
            mitigation.setStoveType(stoveType);
            mitigation.setYear(year);
            mitigation.setUnitsInstalled(unitsThisYear);
            mitigation.setAvoidedEmissions(avoidedEmissions);

            results.add(mitigation);
        }

        // Save all results
        mitigationRepository.saveAll(results);
        return results;
    }
}
