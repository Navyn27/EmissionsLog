package com.navyn.emissionlog.modules.activities.dtos;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Enums.Metrics.MassUnits;
import com.navyn.emissionlog.Enums.Transport.RegionGroup;
import com.navyn.emissionlog.Enums.Transport.TransportModes;
import com.navyn.emissionlog.Enums.Transport.TransportType;
import com.navyn.emissionlog.Enums.Transport.VehicleEngineType;
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

