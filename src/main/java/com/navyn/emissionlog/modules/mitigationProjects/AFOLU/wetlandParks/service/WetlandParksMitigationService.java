package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.service;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models.WetlandParksMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface WetlandParksMitigationService {

    WetlandParksMitigation createWetlandParksMitigation(WetlandParksMitigationDto dto);

    WetlandParksMitigation updateWetlandParksMitigation(UUID id, WetlandParksMitigationDto dto);

    void deleteWetlandParksMitigation(UUID id);

    List<WetlandParksMitigation> getAllWetlandParksMitigation(Integer year, WetlandTreeCategory category);

    Optional<WetlandParksMitigation> getByYearAndCategory(Integer year, WetlandTreeCategory category);

    byte[] generateExcelTemplate();

    Map<String, Object> createWetlandParksMitigationFromExcel(MultipartFile file);
}
