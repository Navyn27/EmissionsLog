package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "urea_emissions")
public class UreaEmissions extends AgricultureAbstractClass {

    private String fertilizerName;
    private double qty;
    private double CO2Emissions;
}