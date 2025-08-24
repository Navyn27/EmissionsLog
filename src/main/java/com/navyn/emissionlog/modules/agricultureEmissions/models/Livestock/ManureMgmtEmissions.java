package com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock;

import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import com.navyn.emissionlog.Enums.Agriculture.OrganicAmendmentTypes;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "manure_mgmt_emissions")
public class ManureMgmtEmissions extends AgricultureAbstractClass {
    @Enumerated(EnumType.STRING)
    private LivestockSpecies species;

    @Enumerated(EnumType.STRING)
    private OrganicAmendmentTypes amendmentType;
    private double population;
    private double totalN;
    private double NAvailable;
    private double N2ONEmissions;
    private double N2OEmissions;
    private double CH4Emissions;
    private double CO2EqEmissions;
}

