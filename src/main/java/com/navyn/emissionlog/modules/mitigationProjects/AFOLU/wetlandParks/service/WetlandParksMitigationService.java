package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.service;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface WetlandParksMitigationService {

    WetlandParksMitigationResponseDto createWetlandParksMitigation(WetlandParksMitigationDto dto);

    WetlandParksMitigationResponseDto updateWetlandParksMitigation(UUID id, WetlandParksMitigationDto dto);

    void deleteWetlandParksMitigation(UUID id);

    List<WetlandParksMitigationResponseDto> getAllWetlandParksMitigation(Integer year, WetlandTreeCategory category);

    Optional<WetlandParksMitigationResponseDto> getByYearAndCategory(Integer year, WetlandTreeCategory category);

    byte[] generateExcelTemplate();

    Map<String, Object> createWetlandParksMitigationFromExcel(MultipartFile file);
}
