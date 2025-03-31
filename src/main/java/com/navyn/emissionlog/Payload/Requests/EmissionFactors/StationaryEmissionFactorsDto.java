package com.navyn.emissionlog.Payload.Requests.EmissionFactors;

import com.navyn.emissionlog.Enums.Emissions;
import lombok.Data;

import java.util.UUID;

@Data
public class StationaryEmissionFactorsDto {
    private Emissions emission;
    private UUID fuel; // Relation to Fuel entity
    private Double energyBasis; // kg CO₂/TJ
    private Double massBasis; // kg CO₂/tonne
    private Double liquidBasis; // kg CO₂/litre (for liquid fuels)
    private Double gasBasis;
}
