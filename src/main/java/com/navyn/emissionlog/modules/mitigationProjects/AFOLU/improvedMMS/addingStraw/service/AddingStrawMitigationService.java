package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AddingStrawMitigationService {

    AddingStrawMitigationResponseDto createAddingStrawMitigation(AddingStrawMitigationDto dto);

    AddingStrawMitigationResponseDto updateAddingStrawMitigation(UUID id, AddingStrawMitigationDto dto);

    void deleteAddingStrawMitigation(UUID id);

    List<AddingStrawMitigationResponseDto> getAllAddingStrawMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createAddingStrawMitigationFromExcel(MultipartFile file);
}
