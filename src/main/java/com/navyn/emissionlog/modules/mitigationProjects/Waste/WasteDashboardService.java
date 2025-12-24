package com.navyn.emissionlog.modules.mitigationProjects.Waste;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.dto.WasteDashboardSummaryDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.dto.WasteDashboardYearDto;
import java.util.List;

public interface WasteDashboardService {
    WasteDashboardSummaryDto getWasteDashboardSummary(Integer startingYear, Integer endingYear);

    List<WasteDashboardYearDto> getWasteDashboardGraph(Integer startingYear, Integer endingYear);

    byte[] exportWasteDashboard(Integer startingYear, Integer endingYear);
}
