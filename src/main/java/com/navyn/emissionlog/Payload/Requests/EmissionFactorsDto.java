package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.Emissions;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
public class EmissionFactorsDto {
    private Emissions emission;
    private UUID fuel; // Relation to Fuel entity
    private Double energyBasis; // kg CO₂/TJ
    private Double massBasis; // kg CO₂/tonne
    private Double liquidBasis; // kg CO₂/litre (for liquid fuels)
    private Double gasBasis;
}
