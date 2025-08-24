package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectManureEmissions;

import com.navyn.emissionlog.Enums.Agriculture.MMS;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper= true)
public class LeachingEmissions extends AgricultureAbstractClass {

    @Enumerated(EnumType.STRING)
    private MMS MMS;

    private String livestockCategory;
    private String livestockSubcategory;
    private double totalNExcretionForMMS;
    private double manureNLossDueToLeachingAndRunoff;

}
