package com.navyn.emissionlog.modules.mitigationProjects.Energy.waterheat.dto;

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

    @NotNull
    private UUID waterHeatParameterId;

}
