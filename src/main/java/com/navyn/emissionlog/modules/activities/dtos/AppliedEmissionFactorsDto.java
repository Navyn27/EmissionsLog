package com.navyn.emissionlog.modules.activities.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO exposing the emission factors applied for an activity (e.g. transport by fuel).
 * Used for transparency and audit (IPCC/GPC reporting).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppliedEmissionFactorsDto {
    private String basis; // e.g. "VOLUME", "MASS"
    private Double fossilCO2EmissionFactor;
    private Double biogenicCO2EmissionFactor;
    private Double ch4EmissionFactor;
    private Double n2OEmissionFactor;
    private String unitPer; // e.g. "L" (per liter), "kg" (per kg)
    private String source; // e.g. "IPCC 2006", "National" - can be extended when factors store source
}
