package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectManureEmissions;

import com.navyn.emissionlog.Enums.Agriculture.LivestockCategory;
import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import com.navyn.emissionlog.Enums.Agriculture.MMS;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class VolatilizationEmissions extends AgricultureAbstractClass {

    @Enumerated(EnumType.STRING)
    private MMS MMS;

    @Enumerated(EnumType.STRING)
    private LivestockSpecies livestockSpecies   ;

    private int animalPopulation = 0;
    private double totalNExcretionForMMS = 0.0;
    private double manureVolatilizationNLoss = 0.0;
    private double indirectVolatilizationN2OEmissionsFromVolatilization = 0.0;
    private double CO2EqEmissions = 0.0;
}
