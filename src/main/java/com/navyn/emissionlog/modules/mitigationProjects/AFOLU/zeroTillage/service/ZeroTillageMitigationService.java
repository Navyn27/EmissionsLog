package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ZeroTillageMitigationService {

    ZeroTillageMitigationResponseDto createZeroTillageMitigation(ZeroTillageMitigationDto dto);

    ZeroTillageMitigationResponseDto updateZeroTillageMitigation(UUID id, ZeroTillageMitigationDto dto);

    void deleteZeroTillageMitigation(UUID id);

    List<ZeroTillageMitigationResponseDto> getAllZeroTillageMitigation(Integer year);

    Optional<ZeroTillageMitigation> getByYear(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createZeroTillageMitigationFromExcel(MultipartFile file);
}
