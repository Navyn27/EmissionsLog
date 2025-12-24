package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models.CropRotationMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface CropRotationMitigationService {

    CropRotationMitigation createCropRotationMitigation(CropRotationMitigationDto dto);

    CropRotationMitigation updateCropRotationMitigation(UUID id, CropRotationMitigationDto dto);

    void deleteCropRotationMitigation(UUID id);

    List<CropRotationMitigation> getAllCropRotationMitigation(Integer year);

    Optional<CropRotationMitigation> getByYear(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createCropRotationMitigationFromExcel(MultipartFile file);
}
