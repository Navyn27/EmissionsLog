package com.navyn.emissionlog.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dashboard Response DTO - contains both summary and time-series data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDto {
    // Summary totals for the selected period
    private DashboardSummaryDto summary;

    // Time-series data for chart (monthly or yearly breakdown)
    private List<DashboardSummaryDto> timeSeries;

    // Metadata
    private Integer startYear;
    private Integer endYear;
    private String mode; // "MONTH" or "YEAR"
}
