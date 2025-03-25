package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Validators.ValidUnitForMetric;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@ValidUnitForMetric
@Data
public class CreateStationaryActivityDto {
    private Sectors sector;
    private UUID fuel;
    private String fuelUnit;
    private Scopes scope;
    private Double fuelAmount;
    private Metrics metric;
    private FuelStates fuelState;
    private ActivityTypes activityType;
    private LocalDateTime activityYear = LocalDateTime.now();
}
