package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface CropRotationParameterService {
    
    /**
     * Creates a new CropRotationParameters
     * @param dto The DTO containing parameter data
     * @return The created parameter as a response DTO
     */
    CropRotationParameterResponseDto create(CropRotationParameterDto dto);
    
    /**
     * Retrieves a CropRotationParameters by ID
     * @param id The UUID of the parameter
     * @return The parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    CropRotationParameterResponseDto getById(UUID id);
    
    /**
     * Retrieves all CropRotationParameters
     * @return List of all parameters as response DTOs
     */
    List<CropRotationParameterResponseDto> getAll();
    
    /**
     * Updates an existing CropRotationParameters
     * @param id The UUID of the parameter to update
     * @param dto The DTO containing updated parameter data
     * @return The updated parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    CropRotationParameterResponseDto update(UUID id, CropRotationParameterDto dto);
    
    /**
     * Disables a CropRotationParameters (sets isActive = false)
     * Instead of deleting, this method soft-deletes it by disabling the parameter
     * @param id The UUID of the parameter to disable
     * @throws RuntimeException if parameter not found
     */
    void disable(UUID id);
    
    /**
     * Retrieves the latest active CropRotationParameters
     * Returns the most recently created parameter that is active
     * @return The latest active parameter as a response DTO
     * @throws RuntimeException if no active parameter found
     */
    CropRotationParameterResponseDto getLatestActive();
}

