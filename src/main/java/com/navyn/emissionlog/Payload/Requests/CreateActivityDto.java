package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.FuelState;
import com.navyn.emissionlog.Enums.Metric;
import com.navyn.emissionlog.Enums.Scopes;
import com.navyn.emissionlog.Models.Sector;
import com.navyn.emissionlog.Validators.ValidUnitForMetric;
import lombok.Data;

import java.util.UUID;

@ValidUnitForMetric
@Data
public class CreateActivityDto {
    private Sector sector;
    private UUID fuel;
    private String fuelUnit;
    private Scopes scope;
    private Double fuelAmount;
    private Metric metric;
    private FuelState fuelState;
}
