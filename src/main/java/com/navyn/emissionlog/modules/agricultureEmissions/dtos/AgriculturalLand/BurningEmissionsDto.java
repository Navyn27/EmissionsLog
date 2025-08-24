package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand;

import com.navyn.emissionlog.Enums.Agriculture.BurningAgentType;
import com.navyn.emissionlog.Enums.Metrics.MassUnits;
import lombok.Data;

@Data
public class BurningEmissionsDto {
    private int year;
    private String activityDesc;
    private double burntArea;
    private String fireType;
    private double fuelMassAvailable = 0.0;
    private MassUnits fuelMassUnit;
    private BurningAgentType burningAgentType;
    private Boolean isEucalyptusForest = false;
}
