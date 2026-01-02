package com.navyn.emissionlog.modules.mitigationProjects;

import com.navyn.emissionlog.modules.mitigationProjects.dtos.MitigationDashboardSummaryDto;
import com.navyn.emissionlog.modules.mitigationProjects.dtos.MitigationDashboardYearDto;

import java.util.List;

public interface MitigationDashboardService {
    
    MitigationDashboardSummaryDto getMitigationDashboardSummary(Integer startingYear, Integer endingYear);
    
    List<MitigationDashboardYearDto> getMitigationDashboardGraph(Integer startingYear, Integer endingYear);
    
    byte[] exportMitigationDashboard(Integer startingYear, Integer endingYear);
}

