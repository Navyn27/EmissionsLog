package com.navyn.emissionlog.modules.activities.dtos;

import com.navyn.emissionlog.Enums.Metrics.Metrics;
import com.navyn.emissionlog.Enums.Sectors;
import com.navyn.emissionlog.Enums.Fuel.FuelStates;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UpdateStationaryActivityDto {
    private Sectors sector;
    private LocalDateTime activityYear;
    private UUID fuel;
    private Double fuelAmount;
    private FuelStates fuelState;
    private Metrics metric;
    private String fuelUnit;
    private UUID region;
}
