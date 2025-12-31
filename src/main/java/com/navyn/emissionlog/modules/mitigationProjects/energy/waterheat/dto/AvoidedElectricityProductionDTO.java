package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class AvoidedElectricityProductionDTO {

    @NotNull
    private Integer year;

    @NotNull
    @Min(1)
    private Integer unitsInstalledThisYear;

    // User input - moved from WaterHeatParameter
    @NotNull
    @Min(1)
    private Integer averageWaterHeat;

    // Foreign key to Intervention table
    private UUID projectInterventionId;

}
