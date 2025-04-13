package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class TransportActivityDto extends CreateStationaryActivityDto{
    public LocalDateTime activityYear = LocalDateTime.now();
    public RegionGroup regionGroup;
    public TransportType transportType;
    public TransportModes transportMode;
    public Integer passengers;
    public Double freightWeight;
    public MassUnits freightWeightUnit;
    public VehicleEngineType vehicleType;
}
