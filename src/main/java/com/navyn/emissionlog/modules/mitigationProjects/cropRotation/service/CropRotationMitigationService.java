package com.navyn.emissionlog.modules.mitigationProjects.cropRotation.service;

import com.navyn.emissionlog.modules.mitigationProjects.cropRotation.dtos.CropRotationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.cropRotation.models.CropRotationMitigation;

import java.util.List;
import java.util.Optional;

public interface CropRotationMitigationService {
    
    CropRotationMitigation createCropRotationMitigation(CropRotationMitigationDto dto);
    
    List<CropRotationMitigation> getAllCropRotationMitigation(Integer year);
    
    Optional<CropRotationMitigation> getByYear(Integer year);
}
