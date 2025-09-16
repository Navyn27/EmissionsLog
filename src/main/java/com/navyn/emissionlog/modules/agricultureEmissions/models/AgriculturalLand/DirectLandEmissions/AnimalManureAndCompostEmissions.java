package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions;

import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import com.navyn.emissionlog.Enums.Agriculture.OrganicAmendmentTypes;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "manure_mgmt_emissions")
public class AnimalManureAndCompostEmissions extends AgricultureAbstractClass {

    @Enumerated(EnumType.STRING)
    private OrganicAmendmentTypes amendmentType;

    @Enumerated(EnumType.STRING)
    private LivestockSpecies livestockSpecies;

    private double population = 0.0;
    private double totalN = 0.0;
    private double NAvailable = 0.0;
    private double N2ONEmissions = 0.0;
    private double N2OEmissions = 0.0;
    private double CH4Emissions = 0.0;
    private double CO2EqEmissions = 0.0;
}

