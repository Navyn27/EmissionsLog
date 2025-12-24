package com.navyn.emissionlog.modules.dashboard;

import com.navyn.emissionlog.utils.DashboardData;

import java.util.List;

/**
 * Service for dashboard data aggregation across all emission modules
 */
public interface DashboardService {
    
    /**
     * Get main dashboard with all emissions, mitigation, and net emissions
     * @param startingYear optional filter
     * @param endingYear optional filter
     * @return DashboardData with comprehensive emissions data
     */
    DashboardData getMainDashboard(Integer startingYear, Integer endingYear);
    
    /**
     * Get yearly graph data for main dashboard
     * @param startingYear start year
     * @param endingYear end year
     * @return List of yearly DashboardData
     */
    List<DashboardData> getMainDashboardGraph(Integer startingYear, Integer endingYear);
    
    /**
     * Get stationary emissions dashboard
     * @param startingYear optional filter
     * @param endingYear optional filter
     * @return DashboardData with stationary emissions only
     */
    DashboardData getStationaryDashboard(Integer startingYear, Integer endingYear);
    
    /**
     * Get waste emissions dashboard
     * @param startingYear optional filter
     * @param endingYear optional filter
     * @return DashboardData with waste emissions only
     */
    DashboardData getWasteDashboard(Integer startingYear, Integer endingYear);
    
    /**
     * Get agriculture emissions dashboard
     * @param startingYear optional filter
     * @param endingYear optional filter
     * @return DashboardData with agriculture emissions only
     */
    DashboardData getAgricultureDashboard(Integer startingYear, Integer endingYear);
    
    /**
     * Get land use emissions dashboard
     * @param startingYear optional filter
     * @param endingYear optional filter
     * @return DashboardData with land use emissions only
     */
    DashboardData getLandUseDashboard(Integer startingYear, Integer endingYear);
}
