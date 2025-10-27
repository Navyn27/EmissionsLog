package com.navyn.emissionlog.modules.wasteEmissions.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class WasteWaterDto extends GeneralWasteByPopulationDto{
    
    @NotNull(message = "EICV report reference is required")
    public UUID eicvReport;
}
