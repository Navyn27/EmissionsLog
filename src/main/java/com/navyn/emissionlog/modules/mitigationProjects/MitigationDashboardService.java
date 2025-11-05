package com.navyn.emissionlog.modules.mitigationProjects;

import com.navyn.emissionlog.utils.DashboardData;
import java.util.List;

public interface MitigationDashboardService {
    
    DashboardData getMitigationDashboardSummary(Integer startingYear, Integer endingYear);
    
    List<DashboardData> getMitigationDashboardGraph(Integer startingYear, Integer endingYear);
}
