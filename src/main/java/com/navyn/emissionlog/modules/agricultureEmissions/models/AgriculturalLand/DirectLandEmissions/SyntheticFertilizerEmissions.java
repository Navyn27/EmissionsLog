package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions;

import com.navyn.emissionlog.Enums.Agriculture.CropTypes;
import com.navyn.emissionlog.Enums.Agriculture.Fertilizers;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "synthetic_fertilizer_emissions")
public class SyntheticFertilizerEmissions extends AgricultureAbstractClass {

    @Enumerated(EnumType.STRING)
    private CropTypes cropType;

    @Enumerated(EnumType.STRING)
    private Fertilizers fertType;

    private double qtyApplied = 0.0;
    private double NAmount = 0.0;
    private double N2ONEmissions = 0.0;
    private double N2OEmissions = 0.0;
    private double CO2EqEmissions = 0.0;
}
