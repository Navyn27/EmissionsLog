package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AddingStrawParameterResponseDto {
    private UUID id;
    private Double emissionPerCow; // CH4 emissions per cow, tonnes CO2e
    private Double reduction; // Reduction of CH4 emissions, %
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

