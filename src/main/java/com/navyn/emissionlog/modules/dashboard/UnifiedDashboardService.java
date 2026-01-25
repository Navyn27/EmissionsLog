package com.navyn.emissionlog.modules.dashboard;

import com.navyn.emissionlog.modules.dashboard.dto.DashboardResponseDto;
import com.navyn.emissionlog.modules.dashboard.dto.DashboardSummaryDto;

import java.util.List;

/**
 * Unified Dashboard Service Interface
 * Provides consistent emission data for both summary cards and charts
 */
public interface UnifiedDashboardService {

    /**
     * Get complete dashboard data including summary and time-series
     * @param startYear Start year for the query
     * @param endYear End year for the query
     * @param mode "MONTH" for monthly breakdown of a single year, "YEAR" for yearly breakdown
     * @return DashboardResponseDto with summary and time-series data
     */
    DashboardResponseDto getDashboard(Integer startYear, Integer endYear, String mode);

    /**
     * Get summary totals only (for stat cards)
     * @param startYear Start year
     * @param endYear End year
     * @return DashboardSummaryDto with aggregated totals
     */
    DashboardSummaryDto getSummary(Integer startYear, Integer endYear);

    /**
     * Get monthly breakdown for a single year (for monthly chart)
     * @param year The year to get monthly data for
     * @return List of 12 DashboardSummaryDto, one per month
     */
    List<DashboardSummaryDto> getMonthlyData(Integer year);

    /**
     * Get yearly breakdown for a year range (for yearly chart)
     * @param startYear Start year
     * @param endYear End year
     * @return List of DashboardSummaryDto, one per year
     */
    List<DashboardSummaryDto> getYearlyData(Integer startYear, Integer endYear);
}
