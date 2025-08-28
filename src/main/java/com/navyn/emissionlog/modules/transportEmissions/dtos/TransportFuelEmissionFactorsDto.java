package com.navyn.emissionlog.modules.transportEmissions.dtos;

import lombok.Data;

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
    private String basis;
}
