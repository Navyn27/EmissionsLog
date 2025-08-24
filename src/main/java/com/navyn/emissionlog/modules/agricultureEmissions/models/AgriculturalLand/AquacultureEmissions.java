package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "aquaculture_emissions")
public class AquacultureEmissions extends AgricultureAbstractClass {
    private String activityDesc;
    private double fishProduction;
    private double N2ONEmissions;
    private double N2OEmissions;
    private double CO2EqEmissions;
}
