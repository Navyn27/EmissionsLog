package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface StreetTreesParameterService {
    
    /**
     * Creates a new StreetTreesParameter
     * @param dto The DTO containing parameter data
     * @return The created parameter as a response DTO
     */
    StreetTreesParameterResponseDto create(StreetTreesParameterDto dto);
    
    /**
     * Retrieves a StreetTreesParameter by ID
     * @param id The UUID of the parameter
     * @return The parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    StreetTreesParameterResponseDto getById(UUID id);
    
    /**
     * Retrieves all StreetTreesParameters
     * @return List of all parameters as response DTOs
     */
    List<StreetTreesParameterResponseDto> getAll();
    
    /**
     * Updates an existing StreetTreesParameter
     * @param id The UUID of the parameter to update
     * @param dto The DTO containing updated parameter data
     * @return The updated parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    StreetTreesParameterResponseDto update(UUID id, StreetTreesParameterDto dto);
    
    /**
     * Disables a StreetTreesParameter (sets isActive = false)
     * Instead of deleting, this method soft-deletes it by disabling the parameter
     * @param id The UUID of the parameter to disable
     * @throws RuntimeException if parameter not found
     */
    void disable(UUID id);
    
    /**
     * Retrieves the latest active StreetTreesParameter
     * Returns the most recently created parameter that is active
     * @return The latest active parameter as a response DTO
     * @throws RuntimeException if no active parameter found
     */
    StreetTreesParameterResponseDto getLatestActive();
}

