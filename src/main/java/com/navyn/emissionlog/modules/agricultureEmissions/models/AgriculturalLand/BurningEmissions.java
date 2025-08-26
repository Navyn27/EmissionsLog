package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand;

import com.navyn.emissionlog.Enums.Agriculture.BurningAgentType;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class BurningEmissions extends AgricultureAbstractClass {

    private double burntArea = 0.0;
    private String fireType;
    private double fuelMassAvailable = 0.0;
    private double fuelMassConsumed = 0.0;
    private double CO2Emissions = 0.0;
    private double CH4Emissions = 0.0;
    private double N2OEmissions = 0.0;
    private double CO2EqEmissions = 0.0;

    @Enumerated(EnumType.STRING)
    private BurningAgentType burningAgentType;
}
