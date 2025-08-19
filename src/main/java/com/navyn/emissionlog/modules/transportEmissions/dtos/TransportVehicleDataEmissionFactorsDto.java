package com.navyn.emissionlog.modules.transportEmissions.dtos;

import lombok.Data;

@Data
public class TransportVehicleDataEmissionFactorsDto {
    public String regionGroup;
    public String vehicle;
    public String size;
    public String weightLaden;
    public String vehicleYear;
    public String fuelType;
    public String fuel;
    public Double CO2EmissionFactor;
    public Double CH4EmissionFactor;
    public Double N2OEmissionFactor;
}
