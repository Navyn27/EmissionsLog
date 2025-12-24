package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementTreesMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface SettlementTreesMitigationService {

    SettlementTreesMitigation createSettlementTreesMitigation(SettlementTreesMitigationDto dto);

    SettlementTreesMitigation updateSettlementTreesMitigation(UUID id, SettlementTreesMitigationDto dto);

    void deleteSettlementTreesMitigation(UUID id);

    List<SettlementTreesMitigation> getAllSettlementTreesMitigation(Integer year);

    Optional<SettlementTreesMitigation> getByYear(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createSettlementTreesMitigationFromExcel(MultipartFile file);
}
