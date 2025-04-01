package com.navyn.emissionlog.Payload.Requests.EmissionFactors;

import com.navyn.emissionlog.Enums.VehicleEngineType;
import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Enums.TransportType;
import lombok.Data;

import java.util.UUID;

@Data
public class TransportFuelEmissionFactorsDto {
    private String regionGroup;
    private String fuel;
    private String fuelType;
    private Double fossilCO2EmissionFactor;
    private Double biogenicCO2EmissionFactor;
    private String transportType;
    private String vehicleEngineType;
    private Double CH4EmissionFactor;
    private Double N2OEmissionFactor;
}
