package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LandfillGasParameterResponseDto {
    private UUID id;
    private Double destructionEfficiencyPercentage; // Percentage (0-100)
    private Double globalWarmingPotentialCh4; // GWP for CH₄
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

