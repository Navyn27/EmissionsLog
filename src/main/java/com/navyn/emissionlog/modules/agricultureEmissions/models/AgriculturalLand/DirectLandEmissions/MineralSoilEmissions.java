package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions;


import com.navyn.emissionlog.Enums.Agriculture.LandUseCategory;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class MineralSoilEmissions extends AgricultureAbstractClass {


    @Enumerated(EnumType.STRING)
    private LandUseCategory initialLandUse;

    @Enumerated(EnumType.STRING)
    private LandUseCategory landUseInReportingYear;

    private double avLossOfSoilC = 0.0;
    private double NMineralisedInMineralSoil = 0.0;
    private double N2OEmissions = 0.0;
    private double N20NEmissions = 0.0;
    private double CO2EqEmissions = 0.0;
}
