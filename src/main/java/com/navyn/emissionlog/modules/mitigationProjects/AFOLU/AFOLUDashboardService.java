package com.navyn.emissionlog.modules.mitigationProjects.AFOLU;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.dto.AFOLUDashboardSummaryDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.dto.AFOLUDashboardYearDto;

import java.util.List;

public interface AFOLUDashboardService {
    AFOLUDashboardSummaryDto getAFOLUDashboardSummary(Integer startingYear, Integer endingYear);
    List<AFOLUDashboardYearDto> getAFOLUDashboardGraph(Integer startingYear, Integer endingYear);
    byte[] exportAFOLUDashboard(Integer startingYear, Integer endingYear);
}

