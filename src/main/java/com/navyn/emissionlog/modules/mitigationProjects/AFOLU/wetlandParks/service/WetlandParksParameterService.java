package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface WetlandParksParameterService {

    /**
     * Creates a new WetlandParksParameter
     * 
     * @param dto The DTO containing parameter data
     * @return The created parameter as a response DTO
     */
    WetlandParksParameterResponseDto create(WetlandParksParameterDto dto);

    /**
     * Retrieves a WetlandParksParameter by ID
     * 
     * @param id The UUID of the parameter
     * @return The parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    WetlandParksParameterResponseDto getById(UUID id);

    /**
     * Retrieves all WetlandParksParameters
     * 
     * @return List of all parameters as response DTOs
     */
    List<WetlandParksParameterResponseDto> getAll();

    /**
     * Updates an existing WetlandParksParameter
     * 
     * @param id  The UUID of the parameter to update
     * @param dto The DTO containing updated parameter data
     * @return The updated parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    WetlandParksParameterResponseDto update(UUID id, WetlandParksParameterDto dto);

    /**
     * Disables a WetlandParksParameter (sets isActive = false)
     * Instead of deleting, this method soft-deletes it by disabling the parameter
     * 
     * @param id The UUID of the parameter to disable
     * @throws RuntimeException if parameter not found
     */
    void disable(UUID id);

    /**
     * Retrieves the latest active WetlandParksParameter
     * Returns the most recently created parameter that is active
     * 
     * @return The latest active parameter as a response DTO
     * @throws RuntimeException if no active parameter found
     */
    WetlandParksParameterResponseDto getLatestActive();
}

