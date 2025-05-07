package com.navyn.emissionlog.Payload.Requests.Activity;

import com.navyn.emissionlog.Enums.*;
import lombok.Data;

@Data
public abstract class TransportActivityDto extends CreateStationaryActivityDto{
    public RegionGroup regionGroup;
    public TransportType transportType;
    public TransportModes transportMode;
    public Integer passengers;
    public VehicleEngineType vehicleType;
}

