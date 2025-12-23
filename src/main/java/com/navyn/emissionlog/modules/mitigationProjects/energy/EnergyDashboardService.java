package com.navyn.emissionlog.modules.mitigationProjects.energy;

import com.navyn.emissionlog.modules.mitigationProjects.energy.dto.EnergyDashboardSummaryDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.dto.EnergyDashboardYearDto;
import java.util.List;

public interface EnergyDashboardService {
    EnergyDashboardSummaryDto getEnergyDashboardSummary(Integer startingYear, Integer endingYear);

    List<EnergyDashboardYearDto> getEnergyDashboardGraph(Integer startingYear, Integer endingYear);

    byte[] exportEnergyDashboard(Integer startingYear, Integer endingYear);
}

