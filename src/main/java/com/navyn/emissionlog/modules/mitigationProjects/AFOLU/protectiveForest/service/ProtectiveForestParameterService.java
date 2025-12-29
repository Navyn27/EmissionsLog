package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface ProtectiveForestParameterService {

    /**
     * Creates a new ProtectiveForestParameter
     * 
     * @param dto The DTO containing parameter data
     * @return The created parameter as a response DTO
     */
    ProtectiveForestParameterResponseDto create(ProtectiveForestParameterDto dto);

    /**
     * Retrieves a ProtectiveForestParameter by ID
     * 
     * @param id The UUID of the parameter
     * @return The parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    ProtectiveForestParameterResponseDto getById(UUID id);

    /**
     * Retrieves all ProtectiveForestParameters
     * 
     * @return List of all parameters as response DTOs
     */
    List<ProtectiveForestParameterResponseDto> getAll();

    /**
     * Updates an existing ProtectiveForestParameter
     * 
     * @param id  The UUID of the parameter to update
     * @param dto The DTO containing updated parameter data
     * @return The updated parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    ProtectiveForestParameterResponseDto update(UUID id, ProtectiveForestParameterDto dto);

    /**
     * Disables a ProtectiveForestParameter (sets isActive = false)
     * Instead of deleting, this method soft-deletes it by disabling the parameter
     * 
     * @param id The UUID of the parameter to disable
     * @throws RuntimeException if parameter not found
     */
    void disable(UUID id);

    /**
     * Retrieves the latest active ProtectiveForestParameter
     * Returns the most recently created parameter that is active
     * 
     * @return The latest active parameter as a response DTO
     * @throws RuntimeException if no active parameter found
     */
    ProtectiveForestParameterResponseDto getLatestActive();
}
