package com.navyn.emissionlog.modules.mitigationProjects.settlementTrees.service;

import com.navyn.emissionlog.modules.mitigationProjects.settlementTrees.dtos.SettlementTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.settlementTrees.models.SettlementTreesMitigation;

import java.util.List;
import java.util.Optional;

public interface SettlementTreesMitigationService {
    
    SettlementTreesMitigation createSettlementTreesMitigation(SettlementTreesMitigationDto dto);
    
    List<SettlementTreesMitigation> getAllSettlementTreesMitigation(Integer year);
    
    Optional<SettlementTreesMitigation> getByYear(Integer year);
}
