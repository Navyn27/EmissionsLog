package com.navyn.emissionlog.modules.mitigationProjects.IPPU;

import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUDashboardSummaryDto;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUDashboardYearDto;

import java.util.List;

public interface IPPUDashboardService {
    IPPUDashboardSummaryDto getIPPUDashboardSummary(Integer startingYear, Integer endingYear);

    List<IPPUDashboardYearDto> getIPPUDashboardGraph(Integer startingYear, Integer endingYear);

    byte[] exportIPPUDashboard(Integer startingYear, Integer endingYear);
}
