package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface KigaliWWTPParameterService {
    
    KigaliWWTPParameterResponseDto createKigaliWWTPParameter(KigaliWWTPParameterDto dto);
    
    KigaliWWTPParameterResponseDto updateKigaliWWTPParameter(UUID id, KigaliWWTPParameterDto dto);
    
    KigaliWWTPParameterResponseDto getKigaliWWTPParameterById(UUID id);
    
    List<KigaliWWTPParameterResponseDto> getAllKigaliWWTPParameters();
    
    void deleteKigaliWWTPParameter(UUID id);
    
    void disable(UUID id);
    
    /**
     * Retrieves the latest active KigaliWWTPParameter
     * Returns the most recently created parameter that is active
     * @return The latest active parameter as a response DTO
     * @throws RuntimeException if no active parameter found
     */
    KigaliWWTPParameterResponseDto getLatestActive();
}

