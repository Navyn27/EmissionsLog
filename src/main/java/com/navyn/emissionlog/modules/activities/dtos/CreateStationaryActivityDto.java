package com.navyn.emissionlog.modules.activities.dtos;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Enums.Fuel.FuelStates;
import com.navyn.emissionlog.Enums.Fuel.FuelTypes;
import com.navyn.emissionlog.Enums.Metrics.Metrics;
import com.navyn.emissionlog.utils.Validators.ValidUnitForMetric;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@ValidUnitForMetric
@Data
public class CreateStationaryActivityDto {
    //Activity related fields
    private Sectors sector;
    private LocalDateTime activityYear = LocalDateTime.now();

    //Fuel related fields
    public FuelTypes fuelType;
    public UUID fuel;
    public Double fuelAmount;
    public FuelStates fuelState;
    public Metrics metric;
    private String fuelUnit;

    private UUID region;
}
