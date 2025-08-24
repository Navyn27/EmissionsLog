package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand;

import com.navyn.emissionlog.Enums.Agriculture.CropTypes;
import com.navyn.emissionlog.Enums.Agriculture.Fertilizers;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "synthetic_fertilizer_emissions")
public class SyntheticFertilizerEmissions extends AgricultureAbstractClass {

    @Enumerated(EnumType.STRING)
    private CropTypes cropType;

    @Enumerated(EnumType.STRING)
    private Fertilizers fertType;

    private double qtyApplied;
    private double NAmount;
    private double N2ONEmissions;
    private double N2OEmissions;
    private double CO2EqEmissions;
}
