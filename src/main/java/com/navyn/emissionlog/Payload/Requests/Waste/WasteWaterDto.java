package com.navyn.emissionlog.Payload.Requests.Waste;

import lombok.Data;

import java.util.UUID;

@Data
public class WasteWaterDto extends GeneralWasteByPopulationDto{
    public UUID eicvReport;
}
