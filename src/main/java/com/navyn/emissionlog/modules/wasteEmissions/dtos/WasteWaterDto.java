package com.navyn.emissionlog.modules.wasteEmissions.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class WasteWaterDto extends GeneralWasteByPopulationDto{
    public UUID eicvReport;
}
