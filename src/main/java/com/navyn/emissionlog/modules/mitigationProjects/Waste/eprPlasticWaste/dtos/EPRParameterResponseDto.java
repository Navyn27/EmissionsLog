package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EPRParameterResponseDto {
    private UUID id;
    private Double recyclingRateWithoutEPR; // percentage as decimal
    private Double recyclingRateWithEPR; // percentage as decimal
    private Double emissionFactor; // tCO2eq
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

