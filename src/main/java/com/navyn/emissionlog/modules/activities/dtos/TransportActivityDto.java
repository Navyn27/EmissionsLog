package com.navyn.emissionlog.modules.activities.dtos;

import com.navyn.emissionlog.Enums.*;
import lombok.Data;

@Data
public abstract class TransportActivityDto extends CreateStationaryActivityDto{
    //Activity related fields
    public Scopes scope;

    //Freight related fields
    public Double freightWeight;
    public MassUnits freightWeightUnit;

    //Passenger related fields
    public Integer passengers;

    //Region related fields
    public RegionGroup regionGroup;

    //Transport related fields
    public TransportType transportType;
    public TransportModes transportMode;
    public VehicleEngineType vehicleType;
}

