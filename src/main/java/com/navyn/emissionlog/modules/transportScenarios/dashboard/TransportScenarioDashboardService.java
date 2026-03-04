package com.navyn.emissionlog.modules.transportScenarios.dashboard;

import com.navyn.emissionlog.modules.transportScenarios.dashboard.dtos.TransportScenarioDashboardSummaryDto;
import com.navyn.emissionlog.modules.transportScenarios.dashboard.dtos.TransportScenarioDashboardYearDto;

import java.util.List;

public interface TransportScenarioDashboardService {
    
    TransportScenarioDashboardSummaryDto getTransportScenarioDashboardSummary(Integer startingYear, Integer endingYear);
    
    List<TransportScenarioDashboardYearDto> getTransportScenarioDashboardGraph(Integer startingYear, Integer endingYear);
    
    byte[] exportTransportScenarioDashboard(Integer startingYear, Integer endingYear);
}

