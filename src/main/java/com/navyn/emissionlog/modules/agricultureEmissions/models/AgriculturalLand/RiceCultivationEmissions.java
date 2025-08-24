package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand;

import com.navyn.emissionlog.Enums.Agriculture.WaterRegime;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rice_cultivation")
public class RiceCultivationEmissions extends AgricultureAbstractClass {
    private String riceEcosystem;


    @Enumerated(EnumType.STRING)
    private WaterRegime waterRegime;
    private double harvestedArea = 0.0; // in hectares
    private int cultivationPeriod = 0; // in days
    private double orgAmendScalingFactor = 0.0;
    private double soilOrCultivarScaling = 0.0;
    private double adjDailyEFEmissions = 0.0;
    private double annualCH4Emissions = 0.0;
    private double CO2EqEmissions = 0.0;
}