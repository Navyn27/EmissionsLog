package com.navyn.emissionlog.modules.activities.dtos;

import com.navyn.emissionlog.Enums.*;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateTransportActivityByVehicleDataDto extends TransportActivityDto{
    private UUID vehicle;
    private Double distanceTravelled = 0.0;
    private DistanceUnits distanceUnit;
    private MobileActivityDataType mobileActivityDataType;
    public Double freightWeight;
    public MassUnits freightWeightUnit;
}
