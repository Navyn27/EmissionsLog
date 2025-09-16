package com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock;

import com.navyn.emissionlog.Enums.Agriculture.LivestockCategory;
import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class ManureManagementEmissions extends AgricultureAbstractClass {

    //fract is fraction
    @Enumerated(EnumType.STRING)
    private LivestockCategory livestockCategory;

    @Enumerated(EnumType.STRING)
    private LivestockSpecies livestockSpecies;

    private double meanAnnualPopulation = 0.0;
    private double directN2OEmissionsFromMMS = 0.0;
    private double CH4EmissionsFromMMS = 0.0;

    private double volatileSolids = 0.0;
    private double dailyVolatileSolidsPerAnimal = 0.0;
    private double CO2EqEmissions = 0.0;
}
