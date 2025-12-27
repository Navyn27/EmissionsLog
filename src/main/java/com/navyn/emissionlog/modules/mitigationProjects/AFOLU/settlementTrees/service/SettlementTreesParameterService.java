package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface SettlementTreesParameterService {

    /**
     * Creates a new SettlementTreesParameter
     * 
     * @param dto The DTO containing parameter data
     * @return The created parameter as a response DTO
     */
    SettlementTreesParameterResponseDto create(SettlementTreesParameterDto dto);

    /**
     * Retrieves a SettlementTreesParameter by ID
     * 
     * @param id The UUID of the parameter
     * @return The parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    SettlementTreesParameterResponseDto getById(UUID id);

    /**
     * Retrieves all SettlementTreesParameters
     * 
     * @return List of all parameters as response DTOs
     */
    List<SettlementTreesParameterResponseDto> getAll();

    /**
     * Updates an existing SettlementTreesParameter
     * 
     * @param id  The UUID of the parameter to update
     * @param dto The DTO containing updated parameter data
     * @return The updated parameter as a response DTO
     * @throws RuntimeException if parameter not found
     */
    SettlementTreesParameterResponseDto update(UUID id, SettlementTreesParameterDto dto);

    /**
     * Disables a SettlementTreesParameter (sets isActive = false)
     * Instead of deleting, this method soft-deletes it by disabling the parameter
     * 
     * @param id The UUID of the parameter to disable
     * @throws RuntimeException if parameter not found
     */
    void disable(UUID id);

    /**
     * Retrieves the latest active SettlementTreesParameter
     * Returns the most recently created parameter that is active
     * 
     * @return The latest active parameter as a response DTO
     * @throws RuntimeException if no active parameter found
     */
    SettlementTreesParameterResponseDto getLatestActive();
}
