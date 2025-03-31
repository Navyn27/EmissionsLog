package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateMobileActivityByFuelDto {
    private TransportModes modeOfTransport;
    private Sectors sector;
    private UUID fuel;
    private String fuelUnit;
    private Scopes scope;
    private Double fuelAmount;
    private Metrics metric;
    private FuelStates fuelState;
    private LocalDateTime activityYear = LocalDateTime.now();
    private RegionGroup regionGroup;
    private UUID region;
}
