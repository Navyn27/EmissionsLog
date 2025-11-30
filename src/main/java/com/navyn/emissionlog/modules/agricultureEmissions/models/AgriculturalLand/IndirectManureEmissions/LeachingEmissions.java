package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectManureEmissions;

import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import com.navyn.emissionlog.Enums.Agriculture.MMS;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Entity
@Data
@EqualsAndHashCode(callSuper= true)
@Getter
public class LeachingEmissions extends AgricultureAbstractClass {

    @Enumerated(EnumType.STRING)
    private MMS MMS;

    private LivestockSpecies livestockSpecies;
    private double totalNExcretionForMMS;
    private double manureNLossDueToLeachingAndRunoff;
    private double CO2EqEmissions;
}
