package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateTransportActivityByFuelDto {
    public Sectors sector;
    public UUID fuel;
    public String fuelUnit;
    public Scopes scope;
    public Double fuelAmount;
    public Metrics metric;
    public LocalDateTime activityYear = LocalDateTime.now();
    public RegionGroup regionGroup;
    public UUID region;
    public VehicleEngineType vehicleType;
    public TransportType transportType;
}
