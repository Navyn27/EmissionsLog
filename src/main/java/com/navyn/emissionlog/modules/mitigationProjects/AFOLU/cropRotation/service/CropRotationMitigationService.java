package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models.CropRotationMitigation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CropRotationMitigationService {
    
    CropRotationMitigation createCropRotationMitigation(CropRotationMitigationDto dto);
    
    CropRotationMitigation updateCropRotationMitigation(UUID id, CropRotationMitigationDto dto);

    void deleteCropRotationMitigation(UUID id);
    
    List<CropRotationMitigation> getAllCropRotationMitigation(Integer year);
    
    Optional<CropRotationMitigation> getByYear(Integer year);
}
