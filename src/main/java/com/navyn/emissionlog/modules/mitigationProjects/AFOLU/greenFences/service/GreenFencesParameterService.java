package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface GreenFencesParameterService {
    
    /**
     * Creates a new GreenFencesParameter
     * @param dto The DTO containing parameter data
     * @return The created parameter as a response DTO
     */
    GreenFencesParameterResponseDto create(GreenFencesParameterDto dto);
    
    /**
     * Retrieves a GreenFencesParameter by ID
     * @param id The UUID of the parameter
     * @return The parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    GreenFencesParameterResponseDto getById(UUID id);
    
    /**
     * Retrieves all GreenFencesParameters
     * @return List of all parameters as response DTOs
     */
    List<GreenFencesParameterResponseDto> getAll();
    
    /**
     * Updates an existing GreenFencesParameter
     * @param id The UUID of the parameter to update
     * @param dto The DTO containing updated parameter data
     * @return The updated parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    GreenFencesParameterResponseDto update(UUID id, GreenFencesParameterDto dto);
    
    /**
     * Disables a GreenFencesParameter (sets isActive = false)
     * Instead of deleting, this method soft-deletes it by disabling the parameter
     * @param id The UUID of the parameter to disable
     * @throws RuntimeException if parameter not found
     */
    void disable(UUID id);
    
    /**
     * Retrieves the latest active GreenFencesParameter
     * Returns the most recently created parameter that is active
     * @return The latest active parameter as a response DTO
     * @throws RuntimeException if no active parameter found
     */
    GreenFencesParameterResponseDto getLatestActive();
}

