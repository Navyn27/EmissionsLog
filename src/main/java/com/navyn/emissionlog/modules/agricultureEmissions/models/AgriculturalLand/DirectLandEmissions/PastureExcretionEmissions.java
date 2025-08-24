package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions;

import com.navyn.emissionlog.Enums.Agriculture.LivestockCategory;
import com.navyn.emissionlog.Enums.Agriculture.MMS;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class PastureExcretionEmissions extends AgricultureAbstractClass {

    @Enumerated(EnumType.STRING)
    private MMS mms;

    private LivestockCategory livestockCategory;

    private double numberOfAnimals = 0.0;
    private double totalNExcretionDeposited = 0.0;
    private double N2OEmissions = 0.0;
    private double N20NEmissions = 0.0;
    private double CO2EqEmissions = 0.0;
}
