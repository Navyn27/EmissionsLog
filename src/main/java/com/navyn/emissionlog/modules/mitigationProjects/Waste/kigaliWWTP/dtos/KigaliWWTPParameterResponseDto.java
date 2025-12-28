package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class KigaliWWTPParameterResponseDto {
    private UUID id;
    private Double methaneEmissionFactor; // kg CH4 per kg COD
    private Double codConcentration; // kg COD per m³
    private Double ch4Gwp100Year; // kg CO2e per kg CH4
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

