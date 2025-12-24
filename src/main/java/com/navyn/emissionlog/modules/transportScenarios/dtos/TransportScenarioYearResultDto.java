package com.navyn.emissionlog.modules.transportScenarios.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransportScenarioYearResultDto {

    private Integer year;
    private Double bauEmissionsTco2;
    private Double alternativeEmissionsTco2;
    private Double totalMitigationTco2;
    private Double reductionPercent;

    /**
     * Helper method to round values to 3 decimal places for consistent API responses
     */
    public void roundValues() {
        if (bauEmissionsTco2 != null) {
            bauEmissionsTco2 = round(bauEmissionsTco2, 3);
        }
        if (alternativeEmissionsTco2 != null) {
            alternativeEmissionsTco2 = round(alternativeEmissionsTco2, 3);
        }
        if (totalMitigationTco2 != null) {
            totalMitigationTco2 = round(totalMitigationTco2, 3);
        }
        if (reductionPercent != null) {
            reductionPercent = round(reductionPercent, 3);
        }
    }

    private double round(double value, int places) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
