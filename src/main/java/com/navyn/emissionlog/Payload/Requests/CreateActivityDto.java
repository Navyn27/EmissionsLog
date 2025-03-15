package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Validators.ValidUnitForMetric;
import lombok.Data;

import java.util.UUID;

@ValidUnitForMetric
@Data
public class CreateActivityDto {
    private Sectors sector;
    private UUID fuel;
    private String fuelUnit;
    private Scopes scope;
    private Double fuelAmount;
    private Metric metric;
    private FuelState fuelState;
    private EmissionType emissionType;
}
