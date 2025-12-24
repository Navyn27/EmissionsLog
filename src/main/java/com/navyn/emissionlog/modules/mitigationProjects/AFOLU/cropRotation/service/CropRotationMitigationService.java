package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface CropRotationMitigationService {

    CropRotationMitigationResponseDto createCropRotationMitigation(CropRotationMitigationDto dto);

    CropRotationMitigationResponseDto updateCropRotationMitigation(UUID id, CropRotationMitigationDto dto);

    void deleteCropRotationMitigation(UUID id);

    List<CropRotationMitigationResponseDto> getAllCropRotationMitigation(Integer year);

    Optional<CropRotationMitigationResponseDto> getByYear(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createCropRotationMitigationFromExcel(MultipartFile file);
}
