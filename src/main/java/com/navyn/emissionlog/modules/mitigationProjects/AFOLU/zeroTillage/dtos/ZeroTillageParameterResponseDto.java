package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ZeroTillageParameterResponseDto {
    private UUID id;
    private Double carbonIncreaseInSoil; // Carbon increase in soil, tonnes C/ha per year
    private Double carbonToC02; // C to CO2 conversion
    private Double emissionFactorFromUrea; // Emission factor from urea
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

