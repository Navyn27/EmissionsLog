package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface ZeroTillageParameterService {
    
    /**
     * Creates a new ZeroTillageParameter
     * @param dto The DTO containing parameter data
     * @return The created parameter as a response DTO
     */
    ZeroTillageParameterResponseDto create(ZeroTillageParameterDto dto);
    
    /**
     * Retrieves a ZeroTillageParameter by ID
     * @param id The UUID of the parameter
     * @return The parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    ZeroTillageParameterResponseDto getById(UUID id);
    
    /**
     * Retrieves all ZeroTillageParameters
     * @return List of all parameters as response DTOs
     */
    List<ZeroTillageParameterResponseDto> getAll();
    
    /**
     * Updates an existing ZeroTillageParameter
     * @param id The UUID of the parameter to update
     * @param dto The DTO containing updated parameter data
     * @return The updated parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    ZeroTillageParameterResponseDto update(UUID id, ZeroTillageParameterDto dto);
    
    /**
     * Disables a ZeroTillageParameter (sets isActive = false)
     * Instead of deleting, this method soft-deletes by disabling the parameter
     * @param id The UUID of the parameter to disable
     * @throws RuntimeException if parameter not found
     */
    void disable(UUID id);
    
    /**
     * Retrieves the latest active ZeroTillageParameter
     * Returns the most recently created parameter that is active
     * @return The latest active parameter as a response DTO
     * @throws RuntimeException if no active parameter found
     */
    ZeroTillageParameterResponseDto getLatestActive();
}

