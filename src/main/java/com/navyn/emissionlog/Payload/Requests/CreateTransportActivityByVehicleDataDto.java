package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateTransportActivityByVehicleDataDto extends TransportActivityDto{
    private UUID vehicle;
    private Double distanceTravelled = 0.0;
    private DistanceUnits distanceUnit;
    private MobileActivityDataType mobileActivityDataType;
}
