package com.navyn.emissionlog.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard Summary DTO - contains aggregated emission data
 * All emission values are in Kilotonnes (Kt)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    // Individual gas emissions (in Kt)
    @Builder.Default
    private Double totalN2OEmissionsKt = 0.0;

    @Builder.Default
    private Double totalCH4EmissionsKt = 0.0;

    @Builder.Default
    private Double totalFossilCO2EmissionsKt = 0.0;

    @Builder.Default
    private Double totalBioCO2EmissionsKt = 0.0;

    // Land use emissions (already in Kt CO2e)
    @Builder.Default
    private Double totalLandUseEmissionsKtCO2e = 0.0;

    // Total CO2 equivalent (in Kt CO2e)
    @Builder.Default
    private Double totalCO2EqEmissionsKtCO2e = 0.0;

    // Mitigation (in Kt CO2e)
    @Builder.Default
    private Double totalMitigationKtCO2e = 0.0;

    // Net emissions = Gross - Mitigation (in Kt CO2e)
    @Builder.Default
    private Double netEmissionsKtCO2e = 0.0;

    // Time period
    private Integer year;
    private String month; // JANUARY, FEBRUARY, etc.
    private String startDate;
    private String endDate;

    /**
     * Calculate CO2 equivalent from individual gases
     * CH4 GWP = 25, N2O GWP = 298
     */
    public void calculateCO2Equivalent() {
        double co2eq = 0.0;

        // CO2 (GWP = 1)
        if (totalFossilCO2EmissionsKt != null) co2eq += totalFossilCO2EmissionsKt;
        if (totalBioCO2EmissionsKt != null) co2eq += totalBioCO2EmissionsKt;

        // CH4 (GWP = 25)
        if (totalCH4EmissionsKt != null) co2eq += totalCH4EmissionsKt * 25.0;

        // N2O (GWP = 298)
        if (totalN2OEmissionsKt != null) co2eq += totalN2OEmissionsKt * 298.0;

        // Land use (already in CO2e)
        if (totalLandUseEmissionsKtCO2e != null) co2eq += totalLandUseEmissionsKtCO2e;

        this.totalCO2EqEmissionsKtCO2e = co2eq;
    }

    /**
     * Calculate net emissions (Gross - Mitigation)
     */
    public void calculateNetEmissions() {
        double gross = totalCO2EqEmissionsKtCO2e != null ? totalCO2EqEmissionsKtCO2e : 0.0;
        double mitigation = totalMitigationKtCO2e != null ? totalMitigationKtCO2e : 0.0;
        this.netEmissionsKtCO2e = gross - mitigation;
    }

    /**
     * Add another DTO's values to this one
     */
    public void add(DashboardSummaryDto other) {
        if (other == null) return;

        this.totalN2OEmissionsKt = safeAdd(this.totalN2OEmissionsKt, other.totalN2OEmissionsKt);
        this.totalCH4EmissionsKt = safeAdd(this.totalCH4EmissionsKt, other.totalCH4EmissionsKt);
        this.totalFossilCO2EmissionsKt = safeAdd(this.totalFossilCO2EmissionsKt, other.totalFossilCO2EmissionsKt);
        this.totalBioCO2EmissionsKt = safeAdd(this.totalBioCO2EmissionsKt, other.totalBioCO2EmissionsKt);
        this.totalLandUseEmissionsKtCO2e = safeAdd(this.totalLandUseEmissionsKtCO2e, other.totalLandUseEmissionsKtCO2e);
        this.totalMitigationKtCO2e = safeAdd(this.totalMitigationKtCO2e, other.totalMitigationKtCO2e);
    }

    private Double safeAdd(Double a, Double b) {
        double valA = (a != null && !Double.isNaN(a) && Double.isFinite(a)) ? a : 0.0;
        double valB = (b != null && !Double.isNaN(b) && Double.isFinite(b)) ? b : 0.0;
        return valA + valB;
    }
}
