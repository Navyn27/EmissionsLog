package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand;

import com.navyn.emissionlog.Enums.Agriculture.LimingMaterials;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "liming")
public class LimingEmissions extends AgricultureAbstractClass {
    @Enumerated(EnumType.STRING)
    private LimingMaterials material;

    private double CaCO3Qty;
    private double CO2Emissions;
}

