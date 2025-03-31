package com.navyn.emissionlog.Payload.Requests.EmissionFactors;

import com.navyn.emissionlog.Enums.EngineType;
import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Enums.TransportType;

import java.util.UUID;

public class TransportFuelEmissionFactorsDto {
    private RegionGroup regionGroup;
    private UUID fuel;
    private Double FossilCO2EmissionFactor;
    private Double BiogenicCO2EmissionFactor;
    private TransportType transportType;
    private EngineType engineType;
    private Double CH4EmissionFactor;
    private Double N2OEmissionFactor;
}
