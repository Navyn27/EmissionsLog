package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectLandEmissions;


import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class AtmosphericDepositionEmissions extends AgricultureAbstractClass {

    private String landUseSubdivision;
    private double syntheticNVolatilized;
    private double organicNAdditions;
    private double excretionsDepositedByGrazingAnimals;
    private double annualN2OFromAtmosphericDeposition;
    private double N2OEmissions;
    private double CO2EqEmissions;

}
