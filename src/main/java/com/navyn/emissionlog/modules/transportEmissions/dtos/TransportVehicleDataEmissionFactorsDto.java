package com.navyn.emissionlog.modules.transportEmissions.dtos;

import lombok.Data;

@Data
public class TransportVehicleDataEmissionFactorsDto {
    private String regionGroup;
    private String vehicle;
    private String size;
    private String weightLaden;
    private String vehicleYear;
    private String fuelType;
    private String fuel;
    private Double CO2EmissionFactor;
    private Double CH4EmissionFactor;
    private Double N2OEmissionFactor;
    private String basis;
}
