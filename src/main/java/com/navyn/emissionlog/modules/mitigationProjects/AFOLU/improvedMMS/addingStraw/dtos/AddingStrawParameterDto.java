package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddingStrawParameterDto {

    @NotNull(message = "Emission per cow is required")
    @DecimalMin(value = "0.0", message = "Emission per cow must be at least 0")
    private Double emissionPerCow; // CH4 emissions per cow, tonnes CO2e

    @NotNull(message = "Reduction is required")
    @DecimalMin(value = "0.0", message = "Reduction must be at least 0")
    private Double reduction; // Reduction of CH4 emissions, %
}

