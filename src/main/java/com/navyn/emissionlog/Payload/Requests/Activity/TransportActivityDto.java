package com.navyn.emissionlog.Payload.Requests.Activity;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.Fuel;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

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

